package darkjet.server.network.player;

import darkjet.server.Leader;

/**
 * Minecraft Packet Handler
 * @author Blue Electric
 */
public final class Player {
	public final Leader leader;
	public final String IP;
	public final int port;
	public final short mtu;
	public final long clientID;
	
	public Player(Leader leader, String IP, int port, short mtu, long clientID) {
		this.leader = leader;
		this.IP = IP;
		this.port = port;
		this.mtu = mtu;
		this.clientID = clientID;
	}
	
	public final void close() {
		
	}

	public final void close(String reason) {
		
	}
}
