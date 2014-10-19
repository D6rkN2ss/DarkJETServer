package darkjet.server.network.packets.minecraft;

public final class PingPacket extends BaseMinecraftPacket {
	public long pingID;
	
	public PingPacket() {
		
	}
	
	public PingPacket(long pingID) {
		this.pingID = pingID;
	}
	
	@Override
	public int getPID() {
		return MinecraftIDs.PING;
	}

	@Override
	public void parse() {
		bb.get();
		pingID = bb.getLong();
	}

	@Override
	public byte[] getResponse() {
		bb.put(MinecraftIDs.PING);
		bb.putLong(pingID);
		
		return bb.array();
	}

}
