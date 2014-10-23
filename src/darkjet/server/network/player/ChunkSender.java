package darkjet.server.network.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import darkjet.server.Logger;
import darkjet.server.level.chunk.Chunk;
import darkjet.server.math.Vector2;
import darkjet.server.network.packets.minecraft.FullChunkDataPacket;

public final class ChunkSender extends Thread {
	public final Player owner;
	
	public final HashMap<Vector2, Chunk> useChunks = new HashMap<>();
	
	private final HashMap<Integer, ArrayList<Vector2>> MapOrder = new HashMap<>();
	private final HashMap<Vector2, Boolean> requestChunks = new HashMap<>();
	private final ArrayList<Integer> orders = new ArrayList<>();
	
	public boolean first = true;
	public int lastCX = 0, lastCZ = 0;
	
	public boolean refreshAllUsed = false;
	
	public ChunkSender(Player owner) {
		this.owner = owner;
	}
	
	@Override
	public final void run() {
		while ( !isInterrupted() ) {
			try {
				updateChunk();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				//It is Interrupted! Time to Out!
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for( Chunk chunk : useChunks.values() ) {
			owner.level.releaseChunk( chunk.x, chunk.z );
		}
	}
	
	private final void updateChunk() throws Exception {
		int centerX = (int) ( (int) Math.floor(owner.getX()) / 16 );
		int centerZ = (int) ( (int) Math.floor(owner.getZ()) / 16 );
		
		if( refreshAllUsed ) {
			for( Chunk chunk : useChunks.values() ) {
				owner.Queue.addMinecraftPacket( new FullChunkDataPacket( useChunks.get(chunk) ) );
			}
			refreshAllUsed = false;
			return;
		}
		
		if( centerX == lastCX && centerZ == lastCZ && !first ) {
			Thread.sleep(100);
			return;
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
				if( !useChunks.containsKey( v ) ) {
					owner.level.requestChunk(v);
				}
				if( !orders.contains(distance) ) {
					orders.add(distance);
				}
			}
		}
		Collections.sort(orders);

		int sendCount = 0;
		for( Integer i : orders ) {
			for( Vector2 v : MapOrder.get(i) ) {
				try {
					if( useChunks.containsKey(v) ) {
						continue;
					}
					sendCount++;
					owner.Queue.send();
					useChunks.put(v, owner.level.getChunk(v.getX(), v.getZ()));
					owner.Queue.addMinecraftPacket( new FullChunkDataPacket( owner.level.getChunk(v.getX(), v.getZ()) ) );
					owner.Queue.send();
					sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Logger.print(Logger.VERBOSE, "send %d chunk(s) to %s", sendCount, owner.name);
		if(first) {
			owner.InitPlayer();
		}
		Vector2[] v2a = useChunks.keySet().toArray(new Vector2[useChunks.keySet().size()] );
		for( int i = 0; i < v2a.length; i++ ) {
			Vector2 v = v2a[i];
			if( !requestChunks.containsKey( v ) ) {
				owner.level.releaseChunk(v);
				useChunks.remove(v);
			}
		}
		first = false;
	}
}