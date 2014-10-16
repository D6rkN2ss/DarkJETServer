package darkjet.server.network.player;

import java.util.HashMap;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

public final class PlayerManager extends BaseManager {
	//<IP, Player>
	public HashMap<String, Player> Players = new HashMap<>();
	
	public PlayerManager(Leader leader) {
		super(leader);
	}
	
	public final void addPlayer(Player player) {
		Players.put(player.IP, player);
	}
	public final boolean existPlayer(String IP) {
		return Players.containsKey(IP);
	}
	public final Player getPlayer(Player player) {
		return Players.get(player);
	}
	public final void removePlayer(String IP) {
		Players.remove(IP);
	}

}
