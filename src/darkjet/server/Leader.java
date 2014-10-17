package darkjet.server;

import darkjet.server.level.LevelManager;
import darkjet.server.network.NetworkManager;
import darkjet.server.network.player.PlayerManager;
import darkjet.server.network.player.chat.ChatManager;
import darkjet.server.tasker.TaskManager;

/**
 * Leader of Managers<br>
 * Warning: Leader must do not anything except create/provide Manager(s).
 * @author Blue Electric
 */
public final class Leader {
	public final NetworkManager network;
	public final PlayerManager player;
	public final TaskManager task;
	public final ChatManager chat;
	public final LevelManager level;
	
	public final long startTime;
	
	public Leader() {
		network = new NetworkManager(this); network.Init();
		player = new PlayerManager(this); player.Init();
		task = new TaskManager(this); task.Init();
		chat = new ChatManager(this); chat.Init();
		level = new LevelManager(this); level.Init();
		
		startTime = System.currentTimeMillis();
	}
	
	public abstract static class BaseManager {
		public final Leader leader;
		public BaseManager(Leader leader) {
			this.leader = leader;
		}
		
		/**
		 * Startup actions
		 */
		public abstract void Init();
		/**
		 * Server is Closing
		 */
		public abstract void onClose();
	}
}