package darkjet.server.level;

import java.util.LinkedList;

import javax.management.RuntimeErrorException;

import darkjet.server.Leader;
import darkjet.server.block.Block;
import darkjet.server.math.Vector;
import darkjet.server.tasker.MethodTask;

/**
 * Block Update Manager for Level
 * @author Blue Electric
 */
public final class BlockManager {
	public final LinkedList<Vector> updateList = new LinkedList<>();
	public final Leader leader;
	public final Level level;
	
	public final MethodTask mt;
	public BlockManager(Leader leader, Level level) {
		this.level = level;
		this.leader = leader;
		
		try {
			mt = new MethodTask(-1, 1, this, "onUpdate");
			leader.task.addTask( mt );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		leader.task.removeTask( mt );
		super.finalize();
	}



	public final void onUpdate(long currentTick) throws Exception {
		while ( !updateList.isEmpty() ) {
			Vector v;
			synchronized (updateList) {
				v = updateList.poll();
			}
			if( level == null ) {
				throw new RuntimeException("WTF level is null");
			}
			Block b = Block.getBlock( level.getBlock(v) );
			if( b == null ) { continue; }
			b.onUpdate(level, v);
		}
	}
	
	public final void onChange(int x, int y, int z, byte id, byte meta) throws Exception {
		Block b = Block.getBlock( level.getBlock(x, y, z) );
		if(b == null) { b = new Block(id); }
		synchronized (updateList) {
			b.getUpdateRange(updateList, x, y, z);
		}
	}

}
