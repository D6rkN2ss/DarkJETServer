package darkjet.server.math;

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
}
