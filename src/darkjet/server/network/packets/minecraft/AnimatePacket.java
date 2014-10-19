package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class AnimatePacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.ANIMATE;
	}
	
	public byte action;
	public int eid;

	@Override
	public void parse() {
		bb.get();
		action = bb.get();
		eid = bb.getInt();
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(6);
		bb.put( MinecraftIDs.ANIMATE );
		bb.put( action );
		bb.putInt( eid );
		
		return bb.array();
	}
	
}
