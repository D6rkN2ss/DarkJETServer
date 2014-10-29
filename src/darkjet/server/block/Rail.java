package darkjet.server.block;

import java.util.HashMap;

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
	//0, WE
	//1, NS
	//2, S Upper N
	//3, N Upper S
	//4, E Upper W
	//5, W Upper E
	//6, W to S
	//7, W to N
	//8, E to N
	//9, E to S
	
	
	public Rail(int id) {
		super(id);
	}
	
	private final boolean UpperConnectAble(Level level, Vector vector, byte UPPER, byte LOWER) {
		return level.getBlock( vector.getSide(UPPER, 1).getSide(Vector.SIDE_UP, 1) ) != 66 ||
				level.getBlock( vector.getSide(LOWER, 1) ) != 66 ||
				level.getBlock( vector.getSide(LOWER, 1).getSide(Vector.SIDE_DOWN, 1) ) != 66;
	}
	
	public final boolean connectAble(Level level, Vector vector) {
		byte meta = level.getBlockMeta(vector);
		return connectAble(level, vector, meta);
	}
	
	public final boolean connectAble(Level level, Vector vector, byte meta) {
		switch( meta ) {
		case 0:
			return level.getBlock( vector.getSide(Vector.SIDE_WEST, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_EAST, 1) ) != 66;
		case 1:
			return level.getBlock( vector.getSide(Vector.SIDE_NORTH, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_SOUTH, 1) ) != 66;
		case 2:
			return UpperConnectAble(level, vector, Vector.SIDE_SOUTH, Vector.SIDE_NORTH);
		case 3:
			return UpperConnectAble(level, vector, Vector.SIDE_NORTH, Vector.SIDE_SOUTH);
		case 4:
			return UpperConnectAble(level, vector, Vector.SIDE_EAST, Vector.SIDE_WEST);
		case 5:
			return UpperConnectAble(level, vector, Vector.SIDE_WEST, Vector.SIDE_EAST);
		case 6:
			return level.getBlock( vector.getSide(Vector.SIDE_WEST, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_SOUTH, 1) ) != 66;
		case 7:
			return level.getBlock( vector.getSide(Vector.SIDE_WEST, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_NORTH, 1) ) != 66;
		case 8:
			return level.getBlock( vector.getSide(Vector.SIDE_EAST, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_SOUTH, 1) ) != 66;
		case 9:
			return level.getBlock( vector.getSide(Vector.SIDE_EAST, 1) ) != 66 || level.getBlock( vector.getSide(Vector.SIDE_NORTH, 1) ) != 66;
		}
		throw new RuntimeException("Unknown Metadata");
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
	
	public final void progressMeta(SidedVector v, byte side, Vector placeVector, Level level, short meta, short normal, short up, short down) throws Exception {
		if(v == null) { return; }
		meta = normal;
		switch( v.side ) {
		case -1:
			level.setBlock(v, level.getBlock(v), (byte) normal);
			break;
		case Vector.SIDE_DOWN:
			level.setBlock(v, level.getBlock(v), (byte) down);
			if( level.getBlock( placeVector.getSide(Vector.SIDE_UP, 1).getSide( Vector.invertSide( side ) , 1) ) == 66 ) {
				meta = down;
			}
			break;
		case Vector.SIDE_UP:
			level.setBlock(v, level.getBlock(v), (byte) up);
			if( level.getBlock( placeVector.getSide(Vector.SIDE_DOWN, 1).getSide( Vector.invertSide( side ) , 1) ) == 66 ) {
				meta = up;
			}
			break;
		}
	}
	
	@Override
	public boolean use(Vector vector, Player player, Level level, short meta,
			int face) throws Exception {
		meta = 0;
		//TODO Complete It
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
