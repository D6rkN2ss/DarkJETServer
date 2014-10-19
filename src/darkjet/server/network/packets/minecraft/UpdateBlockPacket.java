package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class UpdateBlockPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.UPDATE_BLOCK;
	}
	public int x,y,z;
	public byte id,meta;
	
	public UpdateBlockPacket(int x, byte y, int z, byte id, byte meta) {
		this.x = x; this.y = y; this.z = z;
		this.id = id; this.meta = meta;
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(12);
		bb.put( MinecraftIDs.UPDATE_BLOCK );
		bb.putInt( x );
		bb.putInt( z );
		bb.put( (byte) y );
		bb.put( id );
		bb.put( meta );
		
		return bb.array();
	}
	
	
}
