package darkjet.server.network.packets.minecraft;

import darkjet.server.Utils;

public final class ClientHandshakePacket extends BaseMinecraftPacket {
	public int cookie;
	public byte security;
	public short port;
	public short timestamp;
	public long session;
	public long session2;
	
	@Override
	public int getPID() {
		return MinecraftIDs.CLIENT_HANDSHAKE;
	}

	@Override
	public void parse(){
		bb.position(0);
		if(bb.get() != MinecraftIDs.CLIENT_HANDSHAKE){
			throw new RuntimeException(String.format("Trying to decode packet ClientHandShake and received %02X.", bb.array()[0]));
		}
		cookie = bb.getInt();
		security = bb.get();
		port = bb.getShort();
		bb.get(new byte[(int) bb.get()]);
		getDataArray(9);
		timestamp = bb.getShort();
		session2 = bb.getLong();
		session = bb.getLong();
	}

	public byte[] getDataArray(int len){
		for(int i = 0; i < len; i++){
			int l = Utils.getTriad(bb);
			bb.get(new byte[l]);
		}
		return new byte[0];
	}
}
