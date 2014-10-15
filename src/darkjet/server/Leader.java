package darkjet.server;

/**
 * Leader of Managers
 * @author Blue Electric
 */
public final class Leader {
	public Leader() {
		
	}
	
	public static class BaseManager {
		public final Leader leader;
		public BaseManager(Leader leader) {
			this.leader = leader;
		}
	}
}