package darkjet.server;

import darkjet.server.config.ConfigManager;
import darkjet.server.entity.EntityManager;
import darkjet.server.level.LevelManager;
import darkjet.server.network.NetworkManager;
import darkjet.server.network.player.PlayerManager;
import darkjet.server.network.player.chat.ChatManager;
import darkjet.server.tasker.TaskManager;
import darkjet.server.utility.RunningTime;

/**
 * Leader of Managers<br>
 * Warning: Leader must do not anything except create/provide Manager(s).
 * @author Blue Electric
 */
public final class Leader {
	public final ConfigManager config;
	public final NetworkManager network;
	public final PlayerManager player;
	public final TaskManager task;
	public final ChatManager chat;
	public final LevelManager level;
	
	public final long startTime;
	
	public Leader() {
		RunningTime rt = new RunningTime();
		Logger.print(Logger.INFO, "DarkJETServer Version: %s(%s) is Start!", Logger.Version, Logger.CodeName);
		try {
			config = new ConfigManager(this); config.Init();
			network = new NetworkManager(this); network.Init();
			player = new PlayerManager(this); player.Init();
			task = new TaskManager(this); task.Init();
			chat = new ChatManager(this); chat.Init();
			level = new LevelManager(this); level.Init();
		} catch (Exception e) {
			Logger.print(Logger.FATAL, "Failed to Init Manager");
			e.printStackTrace();
			System.exit(0);
			throw new RuntimeException();
		}
		Logger.print(Logger.INFO, "Done in %dms", rt.getRunningTimeinMS());
		startTime = System.currentTimeMillis();
	}
	
	public final void stop() {
		player.onClose();
		network.onClose();
		task.onClose();
		chat.onClose();
		level.onClose();
		config.onClose();
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