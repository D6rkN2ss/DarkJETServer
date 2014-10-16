package darkjet.server.network.minecraft;

public final class ClientConnectPacket extends BaseMinecraftPacket {
	public long clientID;
	public long session;
	public byte security;
	
	@Override
	public int getPID() {
		return MinecraftIDs.CLIENT_CONNECT;
	}

	@Override
	public void parse() {
		bb.get();
		clientID = bb.getLong();
		session = bb.getLong();
		security = bb.get();
	}
	
}
