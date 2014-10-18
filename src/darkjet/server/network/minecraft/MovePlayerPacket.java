package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;

public final class MovePlayerPacket extends BaseMinecraftPacket {
	public int eid;
	public float x;
	public float y;
	public float z;
	public float yaw;
	public float pitch;
	public float bodyYaw;
	public boolean isTeleport = false;
	
	@Override
	public int getPID() {
		return MinecraftIDs.MOVE_PLAYER;
	}

	@Override
	public void parse() {
		bb.get();
		eid = bb.getInt();
		x = bb.getFloat();
		y = bb.getFloat();
		z = bb.getFloat();
		yaw = bb.getFloat();
		pitch = bb.getFloat();
		bodyYaw = bb.getFloat();
		//isTeleport = ((bb.get() & 0x80) > 0);
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate( 2 + (0x04 * 7) );
		bb.put(MinecraftIDs.MOVE_PLAYER);
		bb.putInt(eid);
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(z);
		bb.putFloat(yaw);
		bb.putFloat(pitch);
		bb.putFloat(bodyYaw);
		bb.put((byte) (isTeleport == true ? 0x80 : 0x00));
		
		return bb.array();
	}
	
}
