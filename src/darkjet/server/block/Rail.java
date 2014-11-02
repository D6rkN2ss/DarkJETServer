package darkjet.server.block;

import java.util.HashMap;
import java.util.Queue;

import darkjet.server.Logger;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.network.player.Player;

public class Rail extends Block {
	public final static HashMap<Byte, Byte> MetaMap = new HashMap<>();
	static {
		MetaMap.put(Vector.SIDE_SOUTH, (byte) 0);
		MetaMap.put(Vector.SIDE_NORTH, (byte) 1);
		MetaMap.put(Vector.SIDE_WEST, (byte) 2);
		MetaMap.put(Vector.SIDE_EAST, (byte) 3);
	}
	
	//MAX = 10
	//0, NS
	//1, WE
	//2, E Upper W
	//3, W Upper E
	//4, S Upper N
	//5, N Upper S
	//6, E to S
	//7, W to S
	//8, W to N
	//9, E to N
	
	public Rail(int id) {
		super(id);
	}
	
	public final boolean connectAble(Level level, Vector vector) {
		return connectAble(level, vector, level.getBlock(vector), level.getBlockMeta(vector));
	}
	
	//MAX = 10
	//0, NS
	//1, WE
	//2, E Upper W
	//3, W Upper E
	//4, N Upper S
	//5, S Upper N
	//6, E to S
	//7, W to S
	//8, W to N
	//9, E to N
	
	public final boolean connectAble(Level level, Vector vector, byte id, byte meta) {
		if( id != 66 ) {
			return false;
		}
		/*
		int count = 0;
		for(byte i = Vector.SIDE_NORTH; i < Vector.SIDE_EAST; i++) {
			if(count == 2) { break; }
			if( level.getBlock( vector.getSide(i, 1) ) == 66 ) {
				count++;
			}
		}
		if(count == 2) { return false; }
		*/
		return true;
	}
	
	public final SidedVector getPossibleBlock(Level level, Vector v) {
		SidedVector result = null;
		
		result = new SidedVector(v, (byte) -1);
		Logger.print(Logger.DEBUG, "Possible CUR %d", level.getBlock(result));
		if( level.getBlock(result) != 66 ) {
			result = new SidedVector(v.getSide(Vector.SIDE_DOWN, 1), (byte) Vector.SIDE_DOWN);
			Logger.print(Logger.DEBUG, "Possible DOWN %d", level.getBlock(result));
			if( level.getBlock(result) != 66 ) {
				result = new SidedVector(v.getSide(Vector.SIDE_UP, 1), (byte) Vector.SIDE_UP);
				Logger.print(Logger.DEBUG, "Possible UP %d", level.getBlock(result));
				if( level.getBlock(result) != 66 ) {
					return null;
				}
			}
		}
		
		return result;
	}
	
	
	
	@Override
	public void onUpdate(Level level, Vector v) throws Exception {
		byte meta = progressMeta(level, v);
		if( level.getBlockMeta(v) != meta && meta != -1 ) {
			level.setBlockMeta(v, meta);
		}
	}

	@Override
	public void getUpdateRange(Queue<Vector> updateList, int x, int y, int z) {
		Vector v = new Vector(x, y ,z);
		Vector up = v.getSide(Vector.SIDE_UP, 1);
		Vector down = v.getSide(Vector.SIDE_DOWN, 1);
		updateList.add( v.getSide(Vector.SIDE_NORTH, 1) );
		updateList.add( v.getSide(Vector.SIDE_SOUTH, 1) );
		updateList.add( v.getSide(Vector.SIDE_WEST, 1) );
		updateList.add( v.getSide(Vector.SIDE_EAST, 1) );
		
		updateList.add( up.getSide(Vector.SIDE_NORTH, 1) );
		updateList.add( up.getSide(Vector.SIDE_SOUTH, 1) );
		updateList.add( up.getSide(Vector.SIDE_WEST, 1) );
		updateList.add( up.getSide(Vector.SIDE_EAST, 1) );

		updateList.add( down.getSide(Vector.SIDE_NORTH, 1) );
		updateList.add( down.getSide(Vector.SIDE_SOUTH, 1) );
		updateList.add( down.getSide(Vector.SIDE_WEST, 1) );
		updateList.add( down.getSide(Vector.SIDE_EAST, 1) );
	}

	public final byte progressMeta(Level level, Vector v) {
		byte meta = -1;
		Vector up = v.getSide(Vector.SIDE_UP, 1);
		Vector down = v.getSide(Vector.SIDE_DOWN, 1);
		boolean north = connectAble(level, v.getSide(Vector.SIDE_NORTH, 1));
		boolean upnorth = connectAble(level, up.getSide(Vector.SIDE_NORTH, 1));
		boolean downnorth = connectAble(level, down.getSide(Vector.SIDE_NORTH, 1));
		
		boolean south = connectAble(level, v.getSide(Vector.SIDE_SOUTH, 1));
		boolean upsouth = connectAble(level, up.getSide(Vector.SIDE_SOUTH, 1));
		boolean downsouth = connectAble(level, down.getSide(Vector.SIDE_SOUTH, 1));
		
		boolean west = connectAble(level, v.getSide(Vector.SIDE_WEST, 1));
		boolean upwest = connectAble(level, up.getSide(Vector.SIDE_WEST, 1));
		boolean downwest = connectAble(level, down.getSide(Vector.SIDE_WEST, 1));
		
		boolean east = connectAble(level, v.getSide(Vector.SIDE_EAST, 1));
		boolean upeast = connectAble(level, up.getSide(Vector.SIDE_EAST, 1));
		boolean downeast = connectAble(level, down.getSide(Vector.SIDE_EAST, 1));
		
		//MAX = 10
		//0, NS
		//1, WE
		//2, E Upper W
		//3, W Upper E
		//4, N Upper S
		//5, S Upper N
		//6, E to S
		//7, W to S
		//8, W to N
		//9, E to N
		if( (upeast || east || downeast) && (upnorth || north || downnorth) ) {
			meta = 9;
		} else if( (upwest || west || downwest) && (upnorth || north || downnorth) ) {
			meta = 8;
		} else if( (upwest || west || downwest) && (upsouth || south || downsouth) ) {
			meta = 7;
		} else if( (upeast || east || downeast) && (upsouth || south || downsouth) ) {
			meta = 6;
		} else if( upsouth ) {
			meta = 5;
		} else if( upnorth ) {
			meta = 4;
		} else if( upwest ) {
			meta = 3;
		} else if( upeast ) {
			meta = 2;
		} else if (west || east) {
			meta = 1;
		} else if (north || south){
			meta = 0;
		}
		Logger.print(Logger.DEBUG, "Worked Meta: %d", meta);
		return meta;
	}
	
	
	
	@Override
	public boolean use(Vector vector, Player player, Level level, short meta,
			int face) throws Exception {
		Vector v = vector.getSide((byte) face, 1);
	
		meta = progressMeta(level, v);
		if(meta == -1) { meta = 0; }
		return super.place(vector, player, level, this.id, meta, face);
	}
	
	public final static class SidedVector extends Vector {
		public final byte side;
		
		public SidedVector(Vector v, byte side) {
			super(v.getX(), v.getY(), v.getZ());
			this.side = side;
		}
		
	}
}
