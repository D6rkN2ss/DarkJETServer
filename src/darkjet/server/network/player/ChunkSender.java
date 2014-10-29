package darkjet.server.network.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import darkjet.server.level.chunk.Chunk;
import darkjet.server.math.Vector2;
import darkjet.server.network.packets.minecraft.FullChunkDataPacket;

public final class ChunkSender {
	public final class ChunkCache {
		public final Chunk chunk;
		private boolean sended = false;
		
		public ChunkCache(Chunk chunk) {
			this.chunk = chunk;
		}
		
		public void send() throws Exception {
			if(sended) { return; }
			owner.Queue.send();
			owner.Queue.addMinecraftPacket( new FullChunkDataPacket(chunk) );
			lastChunkSeq = owner.Queue.getSeq();
			owner.Queue.send();
			sended = true;
		}
		public boolean isSended() {
			return sended;
		}
	}
	
	private final Player owner;
	
	protected final HashMap<Vector2, ChunkCache> useChunks = new HashMap<>();
	private final HashMap<Integer, ArrayList<Vector2>> MapOrder = new HashMap<>();
	private final HashMap<Vector2, Boolean> requestChunks = new HashMap<>();
	private final ArrayList<Integer> orders = new ArrayList<>();
	private final LinkedList<ChunkCache> sendChunk = new LinkedList<>();
	private int totalSend = 0;
	private boolean first = true;
	private int lastCX = 0, lastCZ = 0;
	private int lastChunkSeq = -1;
	private final Object lastChunkLock = new Object();
	
	public ChunkSender(Player owner) {
		this.owner = owner;
	}
	
	public final void destroy() {
		for( ChunkCache cc : useChunks.values() ) {
			owner.level.releaseChunk( cc.chunk.x, cc.chunk.z );
		}
	}
	
	public final void ACKReceive(int seq) {
		synchronized (lastChunkLock) {
			if(seq >= lastChunkSeq) {
				lastChunkSeq = -1;
			}
		}
	}
	
	public final boolean updateChunk() throws Exception {
		if( first && totalSend == 56 ) {
			owner.InitPlayer();
			first = false;
		}
		synchronized (lastChunkLock) {
			if( lastChunkSeq != -1 ) { return false; }
		}
		if( !sendFourChunk() ) {
			return refreshChunkList();
		}
		return true;
	}
	
	private final boolean sendFourChunk() throws Exception {
		return sendOneChunk() && sendOneChunk() && sendOneChunk() && sendOneChunk();
	}
	
	private final boolean sendOneChunk() throws Exception {
		ChunkCache cc = sendChunk.poll();
		if( cc == null ) {
			return false;
		}
		if( !cc.isSended() ) {
			cc.send();
			totalSend++;
			Thread.sleep(1);
		} else {
			return sendOneChunk();
		}
		return true;
	}
	
	private final boolean refreshChunkList() {
		int centerX = (int) ( (int) Math.floor(owner.getX()) / 16 );
		int centerZ = (int) ( (int) Math.floor(owner.getZ()) / 16 );
		
		if( centerX == lastCX && centerZ == lastCZ && !first ) {
			return true;
		}
		lastCX = centerX; lastCZ = centerZ;
		int radius = 6;
		
		MapOrder.clear(); requestChunks.clear(); orders.clear();
		
		for (int x = -radius; x <= radius; ++x) {
			for (int z = -radius; z <= radius; ++z) {
				int distance = (x*x) + (z*z);
				int chunkX = x + centerX;
				int chunkZ = z + centerZ;
				Vector2 v = new Vector2(chunkX, chunkZ);
				if( !MapOrder.containsKey( distance ) ) {
					MapOrder.put(distance, new ArrayList<Vector2>());
				}
				requestChunks.put(v, true);
				MapOrder.get(distance).add( v );
				if( !orders.contains(distance) ) {
					orders.add(distance);
				}
			}
		}
		Collections.sort(orders);
		
		for( Integer i : orders ) {
			for( Vector2 v : MapOrder.get(i) ) {
				try {
					if( useChunks.containsKey(v) ) { continue; }
					ChunkCache cc = new ChunkCache( owner.level.requestChunk(v) );
					useChunks.put(v, cc);
					if( !sendChunk.contains(cc) ) {
						sendChunk.add( cc );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		Vector2[] v2a = useChunks.keySet().toArray(new Vector2[useChunks.keySet().size()] );
		for( int i = 0; i < v2a.length; i++ ) {
			Vector2 v = v2a[i];
			if( !requestChunks.containsKey( v ) ) {
				owner.level.releaseChunk(v);
				useChunks.remove(v);
			}
		}
		return false;
	}
}