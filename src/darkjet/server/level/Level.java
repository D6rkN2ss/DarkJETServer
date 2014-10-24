package darkjet.server.level;

import darkjet.server.Leader;
import darkjet.server.level.chunk.Chunk;
import darkjet.server.level.chunk.ChunkGenerator;
import darkjet.server.level.chunk.ChunkProvider;
import darkjet.server.math.Vector;
import darkjet.server.math.Vector2;
import darkjet.server.utility.Utils;

import java.io.File;
import java.util.HashMap;

/**
 * Chunk (Caching) Manager
 * @author Blue Electric
 */
public final class Level {
	private final Leader leader;
	
	public final String Name;
	protected ChunkProvider provider;
	
	public HashMap<Vector2, ChunkContainer> ChunkCaches = new HashMap<>();
	
	public Level(Leader leader, String Name) {
		this.leader = leader;
		this.Name = Name;
	}
	public Level(Leader leader, String Name, ChunkProvider provider) {
		this.leader = leader;
		this.Name = Name;
		this.provider = provider;
	}
	
	public final void load() {
		synchronized (this) {
			if( !leader.level.isExist(Name) ) { return; }
			File levelDir = getLevelPath();
			File Provider = new File( levelDir, "provider");
			
			String ProviderName = new String( Utils.FiletoByteArray( Provider ) );
			@SuppressWarnings("unchecked") //Verify Action?
			Class<ChunkProvider> provider = (Class<ChunkProvider>) leader.level.Providers.get(ProviderName);
			try {
				this.provider = (ChunkProvider) provider.getDeclaredConstructor(Level.class, ChunkGenerator.class).newInstance(this, leader.level.getDefaultGenerator() );
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Unvalid Level Provider");
			}
		}
	}
	
	public final void onClose() {
		synchronized (this) {
			provider.onClose(); save();
		}
	}
	
	public final void save() {
		synchronized (this) {
			File levelDir = getLevelPath();
			levelDir.mkdirs();
			File Provider = new File( levelDir, "provider");
			
			Utils.WriteByteArraytoFile( provider.getName().getBytes() , Provider);
			
			for( ChunkContainer cc : ChunkCaches.values() ) {
				provider.saveChunk(cc.chunk);
			}
			ChunkCaches.clear();
		}
	}
	
	public final static class ChunkContainer {
		public int useCount = 1;
		protected Chunk chunk;
		
		public ChunkContainer(Chunk chunk) {
			this.chunk = chunk;
		}
		
		public Chunk getChunk() {
			useCount++;
			return chunk;
		}
		
		public final boolean release() {
			useCount--;
			return useCount == 0;
		}
	}
	/**
	 * Request for Chunk, This method may take a long time.
	 * @param v2 Vector to Chunk
	 * @return Cached or Generated Position
	 */
	public final Chunk requestChunk(Vector2 v2) {
		if( ChunkCaches.containsKey( v2 ) ) {
			return ChunkCaches.get(v2).getChunk();
		}
		Chunk chunk;
		if( !provider.isGenerated(v2) ) {
			chunk = provider.generateChunk(v2);
		} else {
			chunk = provider.loadChunk(v2);
		}
		ChunkCaches.put(v2, new ChunkContainer(chunk) );
		return chunk;
	}
	public final Chunk requestChunk(int chunkX, int chunkZ) {
		return requestChunk( new Vector2(chunkX, chunkZ) );
	}
	
	public final Chunk getChunk(int chunkX, int chunkZ) {
		return ChunkCaches.get( new Vector2(chunkX, chunkZ) ).chunk;
	}
	
	/**
	 * Release chunk, If Nobody using chunk, Really Release It.
	 * @param v2
	 */
	public final void releaseChunk(Vector2 v2) {
		synchronized (this) {
			if( !ChunkCaches.containsKey(v2) ) {
				return;
			}
			if( ChunkCaches.get(v2).release() ) {
				provider.saveChunk( ChunkCaches.get(v2).chunk );
				ChunkCaches.remove(v2);
			}
		}
	}
	public final void releaseChunk(int chunkX, int chunkZ) {
		releaseChunk( new Vector2(chunkX, chunkZ) );
	}
	
	public final File getLevelPath() {
		return leader.level.getLevelPath(Name);
	}
	
	public final void setBlock(Vector v, byte id, byte meta) {
		setBlock(v.getX(), v.getY(), v.getZ(), id, meta);
	}
	public final void setBlock(int x, int y, int z, byte id, byte meta) {
		synchronized (this) {
			Vector2 cv = new Vector2( x >>4, z >> 4 );
			Chunk chunk = ChunkCaches.get( cv ).chunk;
			if( chunk == null ) {
				chunk = requestChunk( cv );
			}
			while( !chunk.isReady() ) {
				
			}
			int cx = Math.abs(x) % 16;
			int cz = Math.abs(z) % 16;
			//cx = Math.abs(cx); cz = Math.abs(cz);
			chunk.setBlock(cx, (byte) y, cz, id, meta);
		}
	}

	public final byte getBlock(Vector v) {
		return getBlock(v.getX(), v.getY(), v.getZ());
	}
	public final byte getBlock(int x, int y, int z) {
		synchronized (this) {
			Chunk chunk = ChunkCaches.get( new Vector2( x >>4, z >> 4 ) ).chunk;
			while( !chunk.isReady() ) {
				
			}
			int cx = x % 16;
			int cz = z % 16;
			return chunk.getBlock(cx, y, cz);
		}
	}
}
