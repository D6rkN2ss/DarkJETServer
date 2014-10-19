package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class SetHealthPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.SET_HEALTH;
	}
	public byte health;
	
	public SetHealthPacket(byte health) {
		this.health = health;
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate( 2);
		bb.put( MinecraftIDs.SET_HEALTH );
		bb.put( (byte) health );
		
		return bb.array();
	}
}