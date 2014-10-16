package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Connection1Packet extends BaseLoginPacket {
	public long serverID;
	public byte protocolVersion;
	public byte[] nullPayload;
	
	public Connection1Packet(long serverID) {
		this.serverID = serverID;
	}
	
	@Override
	public void parse() {
		PID = bb.get();
		bb.position( bb.position()+16 );
		protocolVersion = bb.get();
		nullPayload = Arrays.copyOfRange(bb.array(), bb.array().length - 18, bb.array().length);
	}

	@Override
	public byte[] getResponse() {
		ByteBuffer response = ByteBuffer.allocate(28);
		byte packetID = (byte) 0x06;
		byte security = 0;
		short mtu = (short) nullPayload.length;
		response.put(packetID);
		response.put(magic);
		response.putLong(serverID);
		response.put(security);
		response.putShort(mtu);
		return response.array();
	}
	
	public final byte getProtocolVersion() {
		return protocolVersion;
	}

}
