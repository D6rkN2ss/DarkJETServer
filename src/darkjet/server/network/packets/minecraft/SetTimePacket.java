package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

import darkjet.server.level.Level;

public final class SetTimePacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.SET_TIME;
	}
	public int time;
	
	public SetTimePacket(int time) {
		this.time = time;
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate( 2 + 0x04 );
		bb.put( MinecraftIDs.SET_TIME );
		bb.putInt( (time / Level.TIME_FULL) * 19200 );
		//TODO 0x00
		bb.put( (byte) 0x80 ); //true
		
		return bb.array();
	}
	
}
