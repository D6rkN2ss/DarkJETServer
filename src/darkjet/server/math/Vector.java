package darkjet.server.math;

/**
 * X, Y, Z
 * @author Blue Electric
 */
public class Vector {
	public static final byte SIDE_DOWN = 0;
	public static final byte SIDE_UP = 1;
	public static final byte SIDE_NORTH = 2;
	public static final byte SIDE_SOUTH = 3;
	public static final byte SIDE_WEST = 4;
	public static final byte SIDE_EAST = 5;
	
	private int x, y, z;

	public Vector(int v){
		this(v, v, v);
	}
	public Vector(int x, int y){
		this(x, y, 0);
	}
	public Vector(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getZ(){
		return z;
	}

	@Override
	public int hashCode() {
		return (x + ":" + y + ":" + z).hashCode();
	}
	public void setX(int v){
		this.x = v;
	}
	public void setY(int v){
		this.y = v;
	}
	public void setZ(int v){
		this.z = v;
	}
	
	public Vector add(Vector delta){
		return merge(this, delta);
	}
	public Vector add(int dx, int dy, int dz){
		return new Vector(x + dx, y + dy, z + dz);
	}
	public Vector subtract(Vector delta){
		return add(delta.multiply(-1));
	}
	public Vector subtract(int dx, int dy, int dz){
		return add(-dx, -dy, -dz);
	}
	public Vector multiply(int k){
		return new Vector(x * k, y * k, z * k);
	}
	public Vector divide(int k){
		return multiply(1 / k);
	}

	public Vector floor(){
		return new Vector((int) x, (int) y, (int) z);
	}
	public Vector abs(){
		return new Vector(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	public double length(){
		return Math.sqrt(Math.pow(x, 2) +  Math.pow(y, 2) + Math.pow(z, 2));
	}
	public double distance(Vector other){
		Vector delta = subtract(other).abs();
		return delta.length();
	}
	public Vector getSide(byte side, int step) {
		switch( side ){
			case SIDE_DOWN:
				return new Vector(x, y - step, z);
			case SIDE_UP:
				return new Vector(x, y + step, z);
			case SIDE_NORTH:
	        return new Vector(x, y, z - step);
	        case SIDE_SOUTH:
	            return new Vector(x, y, z + step);
	        case SIDE_WEST:
	            return new Vector(x - step, y, z);
	        case SIDE_EAST:
	            return new Vector(x + step, y, z);
	        default:
	            return this;
		}
	}
	
	public final static byte invertSide(byte side) {
		switch( side ){
		case SIDE_DOWN:
			return SIDE_UP;
		case SIDE_UP:
			return SIDE_DOWN;
		case SIDE_NORTH:
			return SIDE_SOUTH;
        case SIDE_SOUTH:
            return SIDE_NORTH;
        case SIDE_WEST:
            return SIDE_EAST;
        case SIDE_EAST:
            return SIDE_WEST;
        default:
            return side;
	}
	}
	
	public static Vector merge(Vector... vectors){
		int x = 0;
		int y = 0;
		int z = 0;
		for(Vector vector: vectors){
			x += vector.x;
			y += vector.y;
			z += vector.z;
		}
		return new Vector(x, y, z);
	}
}
