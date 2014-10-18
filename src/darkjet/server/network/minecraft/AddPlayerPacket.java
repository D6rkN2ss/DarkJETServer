package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;

import darkjet.server.network.player.Player;

public final class AddPlayerPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.ADD_PLAYER;
	}
	
	public long clientID;
	public String username;
	public int EID;
	public float x;
	public float y;
	public float z;
	public byte yaw;
	public byte pitch;
	public short unknown1;
	public short unknown2;
	public byte[] metadata = new byte[12];
	
	public AddPlayerPacket(Player player) {
		clientID = player.getClientID();
		username = player.getName();
		EID = 0;
		x = (float) player.getX();
		y = (float) player.getY();
		z = (float) player.getZ();
		yaw = (byte) player.getYaw();
		pitch = (byte) player.getPitch();
	}
	
	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(31 + username.length() + metadata.length);
		bb.put(MinecraftIDs.ADD_PLAYER);
		bb.putLong(clientID);
		bb.putShort((short) username.length());
		bb.put(username.getBytes());
		bb.putInt(EID);
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(z);
		bb.put(yaw);
		bb.put(pitch);
		bb.putShort(unknown1);
		bb.putShort(unknown2);
		
		return bb.array();
	}

	
}
