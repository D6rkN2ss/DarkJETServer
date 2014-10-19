package darkjet.server.math;

/**
 * X, Y, Z
 * @author Blue Electric
 */
public class Vector {
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
