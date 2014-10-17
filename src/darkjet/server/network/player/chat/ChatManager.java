package darkjet.server.network.player.chat;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;
import darkjet.server.network.player.Player;

public final class ChatManager extends BaseManager {
	public ChatManager(Leader leader) {
		super(leader);
	}

	public void handleChat(Player player, String message) {
		for(Player p : leader.player.getPlayers()) {
			try {
				p.sendChat("<" + player.name +"> " + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClose() {
		
	}
	
	@Override
	public void Init() {
		
	}
}
