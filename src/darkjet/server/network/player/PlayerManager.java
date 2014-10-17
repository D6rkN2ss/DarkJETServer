package darkjet.server.network.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.math.Vector2;

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
	public final Collection<Player> getPlayers() {
		return Players.values();
	}
	public final Player getPlayer(String IP) {
		return Players.get(IP);
	}
	public final void removePlayer(String IP) {
		Players.remove(IP);
	}
	public final List<Player> getPlayerInChunk(Level level, Vector pos) {
		ArrayList<Player> Result = new ArrayList<Player>();
		Vector2 v = new Vector2(pos.getX() >> 4, pos.getZ() >> 4);
		for( Player p : getPlayers() ) {
			if( p.getLevel().Name.equals( level.Name ) && p.getUsingChunks().containsKey(v) ) {
				Result.add(p);
			}
		}
		return Result;
	}

	@Override
	public void onClose() {
		
	}

	@Override
	public void Init() {
		
	}
}
