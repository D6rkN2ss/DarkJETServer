package darkjet.server.math;

public class Vector2 {
	private int x, z;

	public Vector2(int v){
		this(v, v);
	}
	public Vector2(int x, int z){
		this.x = x;
		this.z = z;
	}

	public int getX(){
		return x;
	}
	public int getZ(){
		return z;
	}

	public void setX(int v){
		this.x = v;
	}
	public void setZ(int v){
		this.z = v;
	}
}
