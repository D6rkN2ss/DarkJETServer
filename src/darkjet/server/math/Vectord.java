package darkjet.server.math;

/**
 * X, Y, Z with double
 * @author Blue Electric
 */
public class Vectord{
	protected double x, y, z;

	public static Vectord fromYawPitch(double yaw, double pitch){
		return fromYawPitch(yaw, pitch, 1);
	}
	public static Vectord fromYawPitch(double yaw, double pitch, double speed){
		Vectord subject = new Vectord();
		setYawPitchOnSubject(yaw, pitch, speed, subject);
		return subject;
	}

	public static void setYawPitchOnSubject(double yaw, double pitch, Vectord subject){
		setYawPitchOnSubject(yaw, pitch, 1, subject);
	}
	public static void setYawPitchOnSubject(double yaw, double pitch, double speed, Vectord subject){
		yaw = (yaw + 90) * Math.PI / 180;
		pitch = pitch * Math.PI / 180;
		double y = -Math.sin(pitch) * speed;
		double horizDelta = Math.abs(Math.cos(pitch)) * speed;
		double x = -horizDelta * Math.sin(yaw);
		double z = horizDelta * Math.cos(yaw);
		subject.setX(x);
		subject.setY(y);
		subject.setZ(z);
		/*
		 * ===DRAFT===
		 * y = sin(pitch)
		 * horizDelta = |cos(pitch)|
		 * x = horizDelta * (-sin(yaw))
		 * z = horizDelta * cos(yaw)
		 */
	}
	public static YawPitchSet getRelativeYawPitch(Vectord from, Vectord to){
		YawPitchSet set = to.subtract(from).getYawPitch(from.distance(to));
		return set;
	}

	public Vectord(){
		this(0, 0, 0);
	}
	public Vectord(double v){
		this(v, v, v);
	}
	public Vectord(double x, double y){
		this(x, y, 0);
	}
	public Vectord(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public YawPitchSet getYawPitch(){
		return getYawPitch(1);
	}
	public YawPitchSet getYawPitch(double speed){
		double pitch = Math.asin(y / speed);
		double yaw = Math.acos((z / speed) / Math.abs(Math.cos(pitch)));
		return new YawPitchSet(yaw, pitch);
		/*
		 * ===DRAFT===
		 * pitch = asin(y)
		 * horizDelta = |cos(pitch)|
		 * cos(yaw) = z / horizDelta
		 * yaw = acos(z / horizDelta)
		 * 
		 */
	}

	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}

	public void setX(double v){
		this.x = v;
	}
	public void setY(double v){
		this.y = v;
	}
	public void setZ(double v){
		this.z = v;
	}
	public void setCoords(Vectord v){
		setX(v.getX());
		setY(v.getY());
		setZ(v.getZ());
	}

	public Vectord add(Vectord delta){
		return merge(this, delta);
	}
	public Vectord add(double dx, double dy, double dz){
		return new Vectord(x + dx, y + dy, z + dz);
	}
	public Vectord subtract(Vectord delta){
		return add(delta.multiply(-1));
	}
	public Vectord subtract(double dx, double dy, double dz){
		return add(-dx, -dy, -dz);
	}
	public Vectord multiply(double k){
		return new Vectord(x * k, y * k, z * k);
	}
	public Vectord divide(double k){
		return multiply(1 / k);
	}

	public Vector floor(){
		return new Vector((int) x, (int) y, (int) z);
	}
	public Vectord abs(){
		return new Vectord(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	public double length(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}
	public double distance(Vectord other){
		Vectord delta = subtract(other).abs();
		return delta.length();
	}

	public static Vectord merge(Vectord... vectors){
		Vectord base = new Vectord();
		for(Vectord vector: vectors){
			base = new Vectord(base.getX() + vector.getX(), base.getY() + vector.getY(), base.getZ() + vector.getZ());
		}
		return base;
	}

	public static class YawPitchSet{
		private double yaw, pitch;
		public YawPitchSet(double yaw, double pitch){
			this.yaw = yaw;
			this.pitch = pitch;
		}
		public double getYaw(){
			return yaw;
		}
		public void setYaw(double yaw){
			this.yaw = yaw;
		}
		public double getPitch(){
			return pitch;
		}
		public void setPitch(double pitch){
			this.pitch = pitch;
		}
	}

	public double[] toArray(){
		return new double[]{x, y, z};
	}
}
