package darkjet.server.block;

import java.util.HashMap;

import darkjet.server.item.Item;

public abstract class Block extends Item {
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
	public Block(int id) {
		super(id);
	}
	
}
