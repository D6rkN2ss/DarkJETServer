package darkjet.server.network;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

/**
 * Manage Network stuff
 * @author Blue Electric
 */
public final class NetworkManager extends BaseManager {
	public UDPServer server;
	
	public NetworkManager(Leader leader) {
		super(leader);
		server = new UDPServer(this);
	}

}
