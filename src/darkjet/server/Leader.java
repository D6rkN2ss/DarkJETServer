package darkjet.server;

import darkjet.server.network.NetworkManager;

/**
 * Leader of Managers
 * @author Blue Electric
 */
public final class Leader {
	public final NetworkManager network;
	
	public Leader() {
		network = new NetworkManager(this);
	}
	
	public static class BaseManager {
		public final Leader leader;
		public BaseManager(Leader leader) {
			this.leader = leader;
		}
	}
}