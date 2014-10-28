package darkjet.server.block;

import java.util.HashMap;

import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public class Stair extends Block {
	public final static HashMap<Byte, Byte> MetaMap = new HashMap<>();
	static {
		MetaMap.put(Vector.SIDE_SOUTH, (byte) 0);
		MetaMap.put(Vector.SIDE_NORTH, (byte) 1);
		MetaMap.put(Vector.SIDE_WEST, (byte) 2);
		MetaMap.put(Vector.SIDE_EAST, (byte) 3);
	}
	
	public Stair(int id) {
		super(id);
	}

	@Override
	public boolean use(Vector vector, Player player, Level level, short meta,
			int face) throws Exception {
		meta = (short) MetaMap.get( player.getDirection() );
		return super.place(vector, player, level, this.id, meta, face);
	}
	
	

}
