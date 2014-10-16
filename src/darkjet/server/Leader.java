package darkjet.server;

import darkjet.server.network.NetworkManager;
import darkjet.server.network.player.PlayerManager;

/**
 * Leader of Managers<br>
 * Warning: Leader must do not anything except create/provide Manager(s).
 * @author Blue Electric
 */
public final class Leader {
	public final NetworkManager network;
	public final PlayerManager player;
	public final long startTime;
	
	public Leader() {
		network = new NetworkManager(this);
		player = new PlayerManager(this);
		
		startTime = System.currentTimeMillis();
	}
	
	public static class BaseManager {
		public final Leader leader;
		public BaseManager(Leader leader) {
			this.leader = leader;
		}
	}
}