package darkjet.server.item;

import java.util.HashMap;

import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.packets.minecraft.UpdateBlockPacket;
import darkjet.server.network.player.Player;

/**
 * Item Handler
 * @author Blue Electric
 */
public class Item {
	public final static HashMap<Integer, Class<?>> CustomItems = new HashMap<>();
	static {
		CustomItems.put(50, Torch.class);
	}
	public final static Item getItem(int id, short meta, int face) throws Exception {
		if( CustomItems.containsKey(id) ) {
			return (Item) CustomItems.get(id).getDeclaredConstructor(Integer.class, Short.class, Integer.class).newInstance(id, meta, face);
		} else {
			return new Item(id, meta, face);
		}
	}
	
	private final int id, face;
	private final short meta;
	public Item(int id, short meta, int face) {
		this.id = id;
		this.meta = meta;
		this.face = face;
	}
	
	/**
	 * Use Item into Given Level/Position
	 * @param Vector vector
	 * @param Player player
	 * @param level Level
	 * @return Worked?
	 */
	public boolean use(Vector vector, Player player, Level level) throws Exception {
		if( !(face >= 0 && face <= 5) ) {
			return false;
		}
		Vector Target = vector.getSide((byte) face, 1);
		byte TB = level.getBlock(Target);
		if( TB == 0x00 ) {
			UpdateBlockPacket uubp = new UpdateBlockPacket(Target.getX(), (byte) Target.getY(), Target.getZ(), (byte) id, (byte) meta);
			level.setBlock(Target, (byte) id, (byte) meta);
			player.leader.player.broadcastPacket(uubp, false);
			return true;
		}
		return false;
	}
}
