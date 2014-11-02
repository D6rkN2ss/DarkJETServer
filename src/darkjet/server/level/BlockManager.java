package darkjet.server.level;

import java.util.LinkedList;

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
	public final Level level;
	
	public BlockManager(Leader leader, Level level) {
		this.level = level;
		
		try {
			leader.task.addTask( new MethodTask(-1, 5, this, "onUpdate") );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final void onUpdate(long currentTick) throws Exception {
		while ( !updateList.isEmpty() ) {
			Vector v = updateList.poll();
			Block b = Block.getBlock( level.getBlock(v) );
			b.onUpdate(level);
		}
	}
	
	public final void onChange(int x, int y, int z, byte id, byte meta) throws Exception {
		Block b = Block.getBlock( level.getBlock(x, y, z) );
		if(b == null) { b = new Block(id); }
		b.getUpdateRange(updateList, x, y, z);
	}

}
