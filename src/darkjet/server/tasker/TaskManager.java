package darkjet.server.tasker;

import java.util.ArrayList;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

public final class TaskManager extends BaseManager {
	public final static int DEFAULT_TICK = 20;
	
	public final Worker worker = new Worker();
	public final ArrayList<Task> Tasks = new ArrayList<>();

	public TaskManager(Leader leader) {
		super(leader);
		worker.start();
	}
	
	public final void addTask(Task task) {
		Tasks.add(task);
	}
	
	public final class Worker extends Thread {
		public int ctick;
		public final int DEFAULT_SLEEP = 1000 / DEFAULT_TICK;
		
		@Override
		public final void run() {
			while ( !isInterrupted() ) {
				try {
					final long ST = System.currentTimeMillis();
					for(int i = 0; i < Tasks.size(); i++) {
						Task task = Tasks.get(i);
						if( task.delay != 0 ) {
							task.delay--;
							continue;
						}
						task.onRun();
						if( task.tick != -1) { task.tick++; }
						if( task.tick == task.etick ) {
							Tasks.remove(i);
							i--;
							continue;
						}
						task.delay = task.sdelay;
					}
					final int sleep = (int) ( DEFAULT_SLEEP - (int) (System.currentTimeMillis() - ST) );
					sleep(sleep);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClose() {
		while( worker.isAlive() ) {
			worker.interrupt();
		}
	}
}
