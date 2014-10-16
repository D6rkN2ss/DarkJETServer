package darkjet.server.network.minecraft;

public final class PongPacket extends BaseMinecraftPacket {
	public long pingID;
	public long pongID;
	
	public PongPacket() {
		
	}
	public PongPacket(long pingID) {
		this.pingID = pingID;
		pongID = System.currentTimeMillis();
	}
	
	
	
	@Override
	public void parse() {
		bb.get();
		pingID = bb.getLong();
		pongID = bb.getLong();
	}
	@Override
	public byte[] getResponse() {
		bb.put(MinecraftIDs.PONG);
		bb.putLong(pingID);
		bb.putLong(pongID);
		return bb.array();
	}
	@Override
	public int getPID() {
		return MinecraftIDs.PONG;
	}

}