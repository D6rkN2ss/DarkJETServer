package darkjet.server.network.player.chat;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;
import darkjet.server.network.packets.minecraft.MessagePacket;
import darkjet.server.network.player.Player;

/**
 * Basic Chatting Manager
 * @author Blue Electric
 */
public final class ChatManager extends BaseManager {
	public ChatManager(Leader leader) {
		super(leader);
	}

	public void handleChat(Player player, String message) throws Exception {
		broadcastChat("<" + player.name +"> " + message);
	}
	
	public final void broadcastChat(String message, Player... Ignore) throws Exception {
		MessagePacket mp = new MessagePacket(message);
		leader.player.broadcastPacket(mp, false, true, Ignore);
	}

	@Override
	public void onClose() {
		
	}
	
	@Override
	public void Init() {
		
	}
}
