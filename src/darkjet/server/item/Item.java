package darkjet.server.item;

import java.util.HashMap;

import darkjet.server.block.Block;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

/**
 * Item Handler
 * @author Blue Electric
 */
public class Item {
	public final static HashMap<Integer, Class<?>> CustomItems = new HashMap<>();
	static {
		CustomItems.put(259, FlintNSteel.class);
	}
	public final static Item getItem(int id) throws Exception {
		if( CustomItems.containsKey(id) ) {
			return (Item) CustomItems.get(id).getDeclaredConstructor(int.class).newInstance(id);
		} else {
			Item result = (Item) Block.getBlock( id );
			if(result == null) {
				if( id > 128 ) {
					return new Item(id);
				} else {
					return new Block(id);
				}
			} else {
				return result;
			}
		}
	}
	protected int id;
	
	public Item(int id) {
		this.id = id;
	}
	
	public final boolean checkValid(Vector vector) {
		if(vector.getY() < 0 || vector.getY() > 127) {
			return false;
		}
		return true;
	}
	
	public boolean use(Vector vector, Player player, Level level, short meta, int face) throws Exception {
		return false;
	}
	
	public final static boolean place(Vector vector, Player player, Level level, int id, short meta, int face) throws Exception {
		Vector Target = vector.getSide((byte) face, 1);
		byte TB = level.getBlock(Target);
		if( TB == 0x00 ) {
			level.setBlock(Target, (byte) id, (byte) meta);
			return true;
		}
		return false;
	}
}
