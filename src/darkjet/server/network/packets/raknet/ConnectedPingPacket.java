package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;

public final class ConnectedPingPacket extends BaseLoginPacket {
	protected long pingID;
	protected String serverName;
	protected long startTime;
	protected long serverID;
	
	public ConnectedPingPacket() {
		super();
	}
	
	public ConnectedPingPacket(byte[] buffer) {
		super(buffer);
	}
	
	public ConnectedPingPacket(String serverName, long startTime, long serverID) {
		this.serverName = serverName;
		this.startTime = startTime;
		this.serverID = serverID;
	}

	@Override
	public void parse() {
		PID = bb.get();
		pingID = bb.getLong();
		bb.position( bb.position()+16 ); 
	}

	@Override
	public byte[] getResponse() {
		String serverName = "MCCPP;Demo;".concat( this.serverName );
		ByteBuffer bb = ByteBuffer.allocate(35 + serverName.length());
		   
		long pingID = System.currentTimeMillis() - startTime;
		short nameData = (short) serverName.length();
		byte[] serverType = serverName.getBytes();
		   
		bb.put( RaknetIDs.UNCONNECTED_PONG );
		bb.putLong(pingID);
		bb.putLong(serverID);
		bb.put(magic);
		bb.putShort(nameData);
		bb.put(serverType);
		
		return bb.array();
	}

	@Override
	public int getPID() {
		return PID;
	}
	
}
