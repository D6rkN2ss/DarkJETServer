package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class RemovePlayerPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.REMOVE_PLAYER;
	}
	public int eid;
	public long clientID;

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(14);
		bb.put( MinecraftIDs.REMOVE_PLAYER );
		bb.putInt(eid);
		bb.putLong(clientID);
		
		return bb.array();
	}

	
	
}
