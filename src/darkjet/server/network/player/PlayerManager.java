package darkjet.server.network.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.math.Vector2;
import darkjet.server.network.packets.minecraft.AddPlayerPacket;
import darkjet.server.network.packets.minecraft.BaseMinecraftPacket;

/**
 * Manager of Player
 * @author Blue Electric
 */
public final class PlayerManager extends BaseManager {
	//(Not) [MinecraftPacket]Logined Players
	//<IP, Player>
	public ConcurrentHashMap<String, Player> NLPlayers = new ConcurrentHashMap<>();
	public ConcurrentHashMap<String, Player> LPlayers = new ConcurrentHashMap<>();
	
	public PlayerManager(Leader leader) {
		super(leader);
		getPlayerFolder().mkdirs();
	}
	
	public final void addNonLoginPlayer(Player player) {
		NLPlayers.put(player.IP, player);
	}
	public final boolean existNonLoginPlayer(String IP) {
		return NLPlayers.containsKey(IP);
	}
	public final Player getNonLoginPlayer(String IP) {
		return NLPlayers.get(IP);
	}
	public final void removeNonLoginPlayer(String IP) {
		NLPlayers.remove(IP);
	}
	
	public final boolean checkValid(Player player) throws Exception {
		for(Player p : LPlayers.values() ) {
			if( p.name == null || p == player ) { continue; }
			if( p.name.equals(player.name) ) {
				//Timeout?
				if( p.IP.equals( player.IP ) ) {
					p.close("Timeout?");
				} else {
					player.close("Another Position Login");
					return false;
				}
			}
		}
		return true;
	}
	public final void addLoginPlayer(Player player) throws Exception {
		for(Player p: LPlayers.values()) {
			if(p == player) { continue; }
			p.sendChat( player.name + " is Connected!" );
			player.Queue.addMinecraftPacket( new AddPlayerPacket(p) );
		}
		LPlayers.put(player.IP, player);
	}
	public final boolean existLoginPlayer(String IP) {
		return LPlayers.containsKey(IP);
	}
	public final Player getLoginPlayer(String IP) {
		return LPlayers.get(IP);
	}
	public final void removeLoginPlayer(String IP) {
		LPlayers.remove(IP);
	}
	
	public final List<Player> getPlayerInChunk(Level level, Vector pos) {
		ArrayList<Player> Result = new ArrayList<Player>();
		Vector2 v = new Vector2(pos.getX() >> 4, pos.getZ() >> 4);
		for( Player p : LPlayers.values() ) {
			if( p.getLevel().Name.equals( level.Name ) && p.getUsingChunks().containsKey(v) ) {
				Result.add(p);
			}
		}
		return Result;
	}
	public final void broadcastPacket(BaseMinecraftPacket packet, boolean otfen, boolean onlyLogin, Player... Ignore) throws Exception {
		for(Player p : LPlayers.values()) {
			boolean IgnoreIt = false;
			for( Player ip : Ignore ) {
				if( p == ip ) { IgnoreIt = true; break; }
			}
			if(IgnoreIt) { continue; }
			if( otfen ) {
				p.Queue.sendOffenPacket(packet);
			} else {
				p.Queue.addMinecraftPacket(packet);
			}
		}
		if( !onlyLogin ) {
			for(Player p : NLPlayers.values()) {
				boolean IgnoreIt = false;
				for( Player ip : Ignore ) {
					if( p == ip ) { IgnoreIt = true; break; }
				}
				if(IgnoreIt) { continue; }
				if( otfen ) {
					p.Queue.sendOffenPacket(packet);
				} else {
					p.Queue.addMinecraftPacket(packet);
				}
			}
		}
	}
	
	public final File getPlayerFolder() {
		return new File(".", "players");
	}
	
	public final File getPlayerFile(String name) {
		return new File(getPlayerFolder(), name);
	}

	@Override
	public void onClose() {
		for( Player p : LPlayers.values() ) {
			try {
				p.save();
				p.close("Server Close");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void Init() {
		
	}
}
