package darkjet.server.network;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;

import darkjet.server.Logger;
import darkjet.server.network.packets.raknet.AcknowledgePacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.ACKPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.NACKPacket;
import darkjet.server.network.packets.raknet.ConnectedPingPacket;
import darkjet.server.network.packets.raknet.Connection1Packet;
import darkjet.server.network.packets.raknet.Connection2Packet;
import darkjet.server.network.packets.raknet.IncompatibleProtocolPacket;
import darkjet.server.network.packets.raknet.MinecraftDataPacket;
import darkjet.server.network.packets.raknet.RaknetIDs;
import darkjet.server.network.player.Player;

/**
 * Low-level UDP Handler
 * @author Blue Electric
 */
public final class UDPServer {
	public final NetworkManager network;
	private DatagramSocket socket;
	
	public UDPServer(NetworkManager network) throws Exception {
		this.network = network;
		try {
			socket = new DatagramSocket(null);
			socket.bind( new InetSocketAddress( network.leader.config.getServerIP(), network.leader.config.getServerPort() ) );
			socket.setReceiveBufferSize(3939);
			socket.setSendBufferSize(3939);
		} catch (BindException be) {
			Logger.print(Logger.FATAL, "Failed to Bind %s:%d", "0.0.0.0", 19132);
			throw new Exception("Failed to Bind");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final void sendTo(byte[] buffer, DatagramPacket packet) throws Exception {
		sendTo(buffer, packet.getAddress().getHostAddress(), packet.getPort());
	}
	
	public final void sendTo(byte[] buffer, String ip, int port) throws Exception {
		sendTo(buffer, buffer.length, ip, port);
	}
	
	public final void sendTo(byte[] buffer, int length, String ip, int port) throws Exception {
		socket.send( new DatagramPacket(buffer, buffer.length, new InetSocketAddress(ip, port)) );
	}
	private final void receive(DatagramPacket buffer) throws Exception {
		socket.receive(buffer);
		buffer.setData(Arrays.copyOf(buffer.getData(), buffer.getLength()));
	}
	
	public final void Receive(byte[] buffer) throws Exception {
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		dp.setData(buffer);
		receive(dp);
		handlePacket(dp);
	}
	
	private final void handlePacket(DatagramPacket packet) throws Exception {
		int RID = packet.getData()[0];
		
		final String IP = packet.getAddress().toString().replace("/", "");
		//Raknet Create Connection
		if(RID >= RaknetIDs.UNCONNECTED_PING && RID <= RaknetIDs.ADVERTISE_SYSTEM) {
			switch(RID) {
				case RaknetIDs.UNCONNECTED_PING:
				case RaknetIDs.UNCONNECTED_PING_OPEN_CONNECTIONS:
					ConnectedPingPacket pingPk = new ConnectedPingPacket( network.leader.config.getServerName(), network.leader.startTime, 39L );
					pingPk.parse( packet.getData() );
					sendTo( pingPk.getResponse() , packet );
					break;
				case RaknetIDs.OPEN_CONNECTION_REQUEST_1:
					//TODO serverID
					Connection1Packet connect1Pk = new Connection1Packet( 39L );
					connect1Pk.parse( packet.getData() );
					//Verify Protocol
					if( connect1Pk.protocolVersion != RaknetIDs.STRUCTURE ) {
						//TODO serverID
						sendTo( new IncompatibleProtocolPacket(39L, RaknetIDs.STRUCTURE).getResponse(), packet);
						break;
					}
					sendTo( connect1Pk.getResponse(), packet );
					break;
				case RaknetIDs.OPEN_CONNECTION_REQUEST_2:
					//TODO serverID
					Connection2Packet connect2Pk = new Connection2Packet( 39L, (short) packet.getPort() );
					connect2Pk.parse( packet.getData() );
					sendTo( connect2Pk.getResponse(), packet );
					Player player = new Player(network.leader, IP, packet.getPort(), connect2Pk.mtuSize, connect2Pk.clientID);
					network.leader.player.addNonLoginPlayer(player);
					break;
				default:
					throw new RuntimeException("Unknown Packet");
			}
		//Minecraft Data Transfer
		} else if( RID >= RaknetIDs.DATA_PACKET_0 && RID <= RaknetIDs.DATA_PACKET_F ) {
			MinecraftDataPacket mdp = new MinecraftDataPacket();
			mdp.parse( packet.getData() );
			if( network.leader.player.existNonLoginPlayer( IP ) ) {
				network.leader.player.getNonLoginPlayer( IP ).handlePacket(mdp);
			}
			if( network.leader.player.existLoginPlayer( IP ) ) {
				network.leader.player.getLoginPlayer( IP ).handlePacket(mdp);
			}
		//Verify Data Transfer
		} else if( RID == RaknetIDs.ACK || RID == RaknetIDs.NACK ) {
			AcknowledgePacket ACK;
			if( RID == RaknetIDs.ACK ) { ACK = new ACKPacket(); }
			else { ACK = new NACKPacket(); }
			ACK.parse( packet.getData() );
			if( network.leader.player.existNonLoginPlayer( IP ) ) {
				network.leader.player.getNonLoginPlayer( IP ).handleVerfiy(ACK);
			}
			if( network.leader.player.existLoginPlayer( IP ) ) {
				network.leader.player.getLoginPlayer( IP ).handleVerfiy(ACK);
			}
		}
	}

	public void close() {
		socket.close();
	}
}
