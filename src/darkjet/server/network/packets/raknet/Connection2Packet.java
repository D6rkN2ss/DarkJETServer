package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;

public final class Connection2Packet extends BaseLoginPacket {
	public long serverID;
	public short clientPort;
	public byte[] securityCookie;
	public short mtuSize;
	public long clientID;
	
	public Connection2Packet(long serverID, short port) {
		this.serverID = serverID;
		this.clientPort = port;
	}

	@Override
	public void parse() {
		PID = bb.get();
		securityCookie = new byte[4];
		bb.get(securityCookie);
		mtuSize = bb.getShort();
		clientID = bb.getLong();
	}

	@Override
	public byte[] getResponse() {
		ByteBuffer response = ByteBuffer.allocate(30);
		byte packetID = (byte) 0x08;
		short mtu = mtuSize;
		byte security = 0;
		response.put(packetID);
		response.put(magic);
		response.putLong(serverID);
		response.putShort(clientPort);
		response.putShort(mtu);
		response.put(security);
		return response.array();
	}
	
}
