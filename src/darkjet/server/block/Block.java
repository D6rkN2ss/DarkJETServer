package darkjet.server.block;

import java.util.HashMap;

import darkjet.server.item.Item;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public class Block extends Item {
	public final static HashMap<Integer, Class<?>> CustomBlocks = new HashMap<>();
	static {
		CustomBlocks.put(50, Torch.class);
	}
	public final static Block getBlock(int id) throws Exception {
		if( CustomBlocks.containsKey(id) ) {
			return (Block) CustomBlocks.get( id ).getDeclaredConstructor(int.class).newInstance(id);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean use(Vector vector, Player player, Level level, short meta,
			int face) throws Exception {
		return super.place(vector, player, level, id, meta, face);
	}

	public Block(int id) {
		super(id);
	}
	
}
