package darkjet.server.item;

import darkjet.server.Logger;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public final class Torch extends Item {
	public Torch(int id, short meta, int face) {
		super(id, meta, face);
		
		if( id != 50 ) {
			Logger.print(Logger.WARNING, "Not Match ID for Torch: %d != 50", id);
		}
	}

	@Override
	public boolean use(Vector vector, Player player, Level level)
			throws Exception {
		//Prevent Double Torch
		if( level.getBlock(vector) == 50 ) {
			return false;
		}
		return super.use(vector, player, level);
	}
	
	

}
