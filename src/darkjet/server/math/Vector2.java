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
	
	@Override
	public int hashCode() {
		return (x + ":" +z).hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof Vector2) ) { return false; }
		Vector2 v2 = (Vector2) obj;
		return x == v2.x && z == v2.z;
	}
	
	
}
