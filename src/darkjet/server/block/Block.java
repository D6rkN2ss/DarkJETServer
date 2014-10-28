package darkjet.server.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import darkjet.server.item.Item;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public class Block extends Item {
	public final static HashMap<Integer, Class<?>> CustomBlocks = new HashMap<>();
	static {
		CustomBlocks.put(50, Torch.class);
		
		//Stairs
		CustomBlocks.put(53, Stair.class); //WODDEN_STAIR
		CustomBlocks.put(67, Stair.class); //COBBLE_STAIR
		CustomBlocks.put(108, Stair.class); //BRICK_STAIR
		CustomBlocks.put(109, Stair.class); //STONE_BRICK_STAIR
		CustomBlocks.put(114, Stair.class); //NETHER_BLOCK_STAIR
		CustomBlocks.put(128, Stair.class); //SANDSTONE_STAIRS
		CustomBlocks.put(134, Stair.class); //SPRUCE_WOOD_STAIRS
		CustomBlocks.put(135, Stair.class); //BIRCH_WOOD_STAIRS
		CustomBlocks.put(136, Stair.class); //JUNGLE_WOOD_STAIRS
		
		CustomBlocks.put(156, Stair.class); //QUARTZ_STAIRS
		CustomBlocks.put(163, Stair.class); //ACACIA_WOOD_STAIRS
		CustomBlocks.put(164, Stair.class); //DARK_OAK_WOOD_STAIRS
		
		//Rails
		CustomBlocks.put(66, Rail.class);
	}
	public final static Block getBlock(int id) throws Exception {
		if( CustomBlocks.containsKey(id) ) {
			return (Block) CustomBlocks.get( id ).getDeclaredConstructor(int.class).newInstance(id);
		} else {
			return null;
		}
	}
	
	public void getUpdateRange(List<Vector> updateList, int x, int y, int z) {
		Vector v = new Vector(x, y, z);
		updateList.add(v.getSide(Vector.SIDE_UP, 1));
		updateList.add(v.getSide(Vector.SIDE_DOWN, 1));
		updateList.add(v.getSide(Vector.SIDE_EAST, 1));
		updateList.add(v.getSide(Vector.SIDE_WEST, 1));
		updateList.add(v.getSide(Vector.SIDE_NORTH, 1));
		updateList.add(v.getSide(Vector.SIDE_SOUTH, 1));
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
