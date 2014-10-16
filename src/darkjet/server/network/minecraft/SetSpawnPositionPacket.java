package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;

import darkjet.server.math.Vector;

public final class SetSpawnPositionPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.SET_SPAWN_POSITION;
	}
	
	protected Vector vector;
	
	public SetSpawnPositionPacket(Vector vector) {
		this.vector = vector;
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate( 1 + (Integer.BYTES * 2) + 1 );
		bb.put( MinecraftIDs.SET_SPAWN_POSITION );
		bb.putInt( vector.getX() );
		bb.putInt( vector.getZ() );
		bb.put( (byte) vector.getY() );
		
		return bb.array();
	}
}
