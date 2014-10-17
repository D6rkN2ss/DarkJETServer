package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;

import darkjet.server.Utils;
import darkjet.server.level.chunk.Chunk;

public final class FullChunkDataPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.FULL_CHUNK_DATA_PACKET;
	}
	public Chunk chunk;
	
	public FullChunkDataPacket(Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public byte[] getResponse() {		
		byte[] compressed;
		try {
			compressed = chunk.getCompressed();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		bb = ByteBuffer.allocate( 1 + compressed.length );
		bb.put( MinecraftIDs.FULL_CHUNK_DATA_PACKET );
		bb.put(compressed);
		return bb.array();
	}
	

	
}
