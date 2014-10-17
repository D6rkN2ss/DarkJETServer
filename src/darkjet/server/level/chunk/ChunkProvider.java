package darkjet.server.level.chunk;

import java.io.File;

import darkjet.server.level.Level;
import darkjet.server.math.Vector2;

/**
 * Chunk Provider<br>
 * Chunk Cache must Merged in Level
 * @author Blue Electric
 */
public abstract class ChunkProvider {
	protected final Level level;
	protected ChunkGenerator generator;
	protected final File ChunkDir;
	
	public ChunkProvider(Level level, ChunkGenerator generator) {
		this.level = level;
		this.generator = generator;
		
		ChunkDir = new File( level.getLevelPath(), "chunks" );
		ChunkDir.mkdirs();
	}
	
	public abstract Chunk loadChunk(int x, int z);
	public final Chunk loadChunk( Vector2 vector ) {
		return loadChunk( vector.getX(), vector.getZ() );
	}
	public final Chunk generateChunk(int x, int z) {
		Chunk chunk = getEmptyChunk(x, z);
		generator.generateChunk( chunk );
		saveChunk(chunk);
		return chunk;
	}
	public final Chunk generateChunk(Vector2 v2) {
		return generateChunk(v2.getX(), v2.getZ());
	}
	public abstract boolean saveChunk(Chunk chunk);
	public abstract boolean isGenerated(int x, int z);
	public final boolean isGenerated( Vector2 vector ) {
		return isGenerated( vector.getX(), vector.getZ() );
	}
	public abstract Chunk getEmptyChunk(int x, int z);
	public abstract String getName();
	public abstract void onClose();
}
