package darkjet.server.network.player;

/**
 * Minecraft Packet Handler
 * @author Blue Electric
 */
public final class Player {
	public final String IP;
	public final int port;
	public final short mtu;
	public final long clientID;
	
	public Player(String IP, int port, short mtu, long clientID) {
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
