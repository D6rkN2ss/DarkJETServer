package darkjet.server.block;

import java.util.HashMap;
import darkjet.server.Logger;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public final class Torch extends Block {
	public final static HashMap<Byte, Byte> MetaMap = new HashMap<>();
	static {
		MetaMap.put(Vector.SIDE_EAST, (byte) 1);
		MetaMap.put(Vector.SIDE_WEST, (byte) 2);
		MetaMap.put(Vector.SIDE_SOUTH, (byte) 3);
		MetaMap.put(Vector.SIDE_NORTH, (byte) 4);
		MetaMap.put(Vector.SIDE_UP, (byte) 5);
	}
	
	public Torch(int id) {
		super(id);
		
		if( id != 50 ) {
			Logger.print(Logger.WARNING, "Not Match ID for Torch: %d != 50", id);
		}
	}

	@Override
	public boolean use(Vector vector, Player player, Level level, short meta, int face)
			throws Exception {
		meta = 0;
		byte id = level.getBlock(vector);
		if( id == (byte) 50 ) {
			if(face == Vector.SIDE_DOWN || face == Vector.SIDE_UP) {
				return false;
			}
		} else {
			if( face != Vector.SIDE_DOWN && face != Vector.SIDE_UP ) {
				meta = (short) MetaMap.get( (byte) face );
			}
		}
		return super.place(vector, player, level, this.id, meta, face);
	}
	
	

}
