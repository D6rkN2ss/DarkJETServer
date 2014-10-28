package darkjet.server.level;

/**
 * Block Update Manager for Level
 * @author Blue Electric
 */
public final class BlockManager {
	public final Level level;
	
	public BlockManager(Level level) {
		this.level = level;
	}
	
	public final void onChange(int x, int y, int z, byte id, byte meta) {
		
	}

}
