package darkjet.server.network.minecraft;

public final class LoginPacket extends BaseMinecraftPacket {
	public String username;
	public int protocol;
	public int protocol2;
	public int clientID;
	public String loginData;
	
	@Override
	public int getPID() {
		return MinecraftIDs.LOGIN;
	}
	
	@Override
	public final void parse() {
		if(bb.get() != MinecraftIDs.LOGIN){
			throw new RuntimeException(String.format("Trying to decode packet LoginPacket and received %02X.", bb.array()[0]));
		}
		username = getString();
		protocol = bb.getInt();
		protocol2 = bb.getInt();
		clientID = bb.getInt();
		loginData = getString();
	}

}
