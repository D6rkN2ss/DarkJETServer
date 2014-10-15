package darkjet.server.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import darkjet.server.network.packets.raknet.RaknetIDs;

/**
 * Low-level UDP Handler
 * @author Blue Electric
 */
public final class UDPServer {
	private DatagramSocket socket;
	
	public UDPServer() {
		try {
			socket = new DatagramSocket(null);
			socket.bind( new InetSocketAddress(19132) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final void sendTo(byte[] buffer, String ip, int port) throws Exception {
		socket.send( new DatagramPacket(buffer, buffer.length, new InetSocketAddress(ip, port)) );
	}
	
	private final void receive(DatagramPacket buffer) throws Exception {
		socket.receive(buffer);
		buffer.setData(Arrays.copyOf(buffer.getData(), buffer.getLength()));
	}
	
	public final void progressReceive() {
		try {
			byte[] buffer = new byte[1539];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
			while( !Thread.interrupted() ) {
				dp.setData(buffer);
				receive(dp);
				handlePacket(dp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final void handlePacket(DatagramPacket packet) {
		int RID = packet.getData()[0];
		
		//Raknet Create Connection
		if(RID >= RaknetIDs.UNCONNECTED_PING && RID <= RaknetIDs.ADVERTISE_SYSTEM) {
			switch(RID) {
				case RaknetIDs.UNCONNECTED_PING:
				case RaknetIDs.UNCONNECTED_PING_OPEN_CONNECTIONS:
					break;
				case RaknetIDs.OPEN_CONNECTION_REQUEST_1:
					break;
				case RaknetIDs.OPEN_CONNECTION_REQUEST_2:
					break;
				default:
					throw new RuntimeException("Unknown Packet");
			}
		//Minecraft Data Transfer
		} else if( RID >= RaknetIDs.DATA_PACKET_0 && RID <= RaknetIDs.DATA_PACKET_F ) {
			//TODO Handle Packet with Player
		//Verify Data Transfer
		} else if( RID == RaknetIDs.ACK || RID == RaknetIDs.NACK ) {
			//TODO Verify Packet with Player
		}
	}
}
