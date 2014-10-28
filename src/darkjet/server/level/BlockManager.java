package darkjet.server.level;

import java.util.ArrayList;

import darkjet.server.block.Block;
import darkjet.server.math.Vector;

/**
 * Block Update Manager for Level
 * @author Blue Electric
 */
public final class BlockManager {
	public final ArrayList<Vector> updateList = new ArrayList<>();
	public final Level level;
	
	public BlockManager(Level level) {
		this.level = level;
	}
	
	public final void onChange(int x, int y, int z, byte id, byte meta) throws Exception {
		Block b = Block.getBlock(id);
		b.getUpdateRange(updateList, x, y, z);
	}

}
