package darkjet.server.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;

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
		
	}
}
