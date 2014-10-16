package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;

public final class IncompatibleProtocolPacket extends BaseLoginPacket {
	public long serverID;
	public byte correctProtocol;
	
	public IncompatibleProtocolPacket(long serverID, byte correctProtocol) {
		this.serverID = serverID;
		this.correctProtocol = correctProtocol;
	}

	@Override
	public byte[] getResponse() {
		ByteBuffer bb = ByteBuffer.allocate(26);
		bb.put( RaknetIDs.INCOMPATIBLE_PROTOCOL_VERSION ); //PacketID
		bb.put(correctProtocol);
		bb.put(magic);
		bb.putLong( serverID );
		
		return bb.array();
	}
	
	
}
