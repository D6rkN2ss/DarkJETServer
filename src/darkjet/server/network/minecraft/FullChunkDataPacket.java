package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;
import darkjet.server.Utils;

public final class FullChunkDataPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.FULL_CHUNK_DATA_PACKET;
	}
	
	public static final byte[] FLATREPEAT = new byte[]{0x07, 0x03, 0x03, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static final byte[] BIOMECOLOR = new byte[]{0x00 ,(byte) 0x85 ,(byte) 0xb2 ,0x4a};
	
	public static final byte[] SUPERFLAT;
	
	static {
		ByteBuffer builder = ByteBuffer.allocate( 0x8000 + 0x4000 + 0x4000 + 0x4000 + 0x100 + 0x400 );
		int last = 0x8000;
		while( builder.position() != last ) {
			builder.put( FLATREPEAT );
		}
		last += 0x4000 + 0x4000 + 0x4000;
		while( builder.position() != last ) {
			builder.put((byte) 0x00);
		}
		last += 0x100;
		while( builder.position() != last ) {
			builder.put((byte) 0xff);
		}
		last += 0x400;
		while( builder.position() != last ) {
			builder.put(BIOMECOLOR);
		}
		SUPERFLAT = builder.array();
	}
	
	public int chunkX, chunkZ;
	
	public FullChunkDataPacket(int x, int z) {
		chunkX = x;
		chunkZ = z;
		//System.out.println( "chunkX: " + x +" chunkZ: " + chunkZ );
	}

	@Override
	public byte[] getResponse() {
		byte[] chunkBuf = SUPERFLAT;
		
		byte[] compressed;
		try {
			compressed = Utils.compressByte( Utils.LInt(chunkX), Utils.LInt(chunkZ),chunkBuf );
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