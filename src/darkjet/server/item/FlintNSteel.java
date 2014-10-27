package darkjet.server.item;

import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public final class FlintNSteel extends Item {
	public FlintNSteel(int id) {
		super(id);
	}

	@Override
	public boolean use(Vector vector, Player player, Level level, short meta, int face)
			throws Exception {
		return super.use(vector, player, level, 51, (short) 0, face);
	}

}
