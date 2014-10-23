package darkjet.server.tasker;

import java.util.ArrayList;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

/**
 * Manager of Task
 * @author Blue Electric
 */
public final class TaskManager extends BaseManager {
	/**
	 * Thread for Register to TaskManager
	 * @author Blue Electric
	 */
	public abstract static class TaskThread extends Thread {
		private boolean isClose = false;
		/**
		 * Mark it be closed
		 */
		@Override
		public final void run() {
			if(!isClose) {
				launch();
			}
		}
		public abstract void launch();
		public final void close() {
			isClose = true;
		}
	}
	
	public final static int DEFAULT_TICK = 20;
	
	public final Worker worker = new Worker();
	public final ArrayList<Task> Tasks = new ArrayList<>();
	public final ArrayList<TaskThread> TaskThreads = new ArrayList<>();

	public TaskManager(Leader leader) {
		super(leader);
		worker.start();
	}
	
	public final void addTask(Task task) {
		synchronized (Tasks) {
			Tasks.add(task);
		}
	}
	
	public final void addThread(TaskThread tt) {
		synchronized (TaskThreads) {
			TaskThreads.add(tt);
			tt.start();
		}
	}
	
	public final void removeTask(Task t) {
		synchronized (Tasks) {
			Tasks.remove(t);
		}
	}
	
	public final class Worker extends Thread {
		public int ctick;
		public static final int DEFAULT_SLEEP = 1000 / DEFAULT_TICK;
		
		@Override
		public final void run() {
			while ( !isInterrupted() ) {
				try {
					final long ST = System.currentTimeMillis();
					synchronized (Tasks) {
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
					}
					synchronized (TaskThreads) {
						for(int i = 0; i < TaskThreads.size(); i++) {
							TaskThread tt = TaskThreads.get(i);
							if(tt.isClose) {
								if( !tt.isAlive() ) {
									TaskThreads.remove(i);
									i--;
									continue;
								} else {
									tt.interrupt();
								}
							}
						}
					}
					final int sleep = (int) ( DEFAULT_SLEEP - (int) (System.currentTimeMillis() - ST) );
					if( sleep > 0 ) {
						sleep(sleep);
					}
				} catch (InterruptedException ie) {
					
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

	@Override
	public void Init() {
		
	}
}
