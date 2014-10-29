package darkjet.server.entity;

import darkjet.server.Leader;
import darkjet.server.Logger;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.tasker.MethodTask;

/**
 * Base Class for Entity
 * @author Blue Electric
 */
public abstract class Entity {
	
	public final Leader leader;
	protected float x, y, z, yaw, pitch, bodyYaw;
	protected float lastX, lastY, lastZ, lastYaw, lastPitch, lastBodyYaw;
	protected int EID;
	
	private MethodTask mt;
	
	public Entity(Leader leader) {
		this.leader = leader;
		
		if( isNeedUpdate() ) {
			try {
				mt = new MethodTask(-1, 1, this, "update");
				leader.task.addTask( mt );
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	
	public void Init(Level level, int EID) {
		this.EID = EID;
		level.entites.addEntity(this);
	}
	
	public void close() throws Exception {
		if( isNeedUpdate() ) {
			leader.task.removeTask(mt);
		}
	}
	
	public final byte getDirection() {
		Logger.print(Logger.DEBUG, "getDirection, yaw mod 360 = %d", (int) (yaw % 360) );
		int rot = (int) ((yaw - 90) % 360);
		if(rot < 0) { rot += 360; }
		
		if( (0 <= rot && rot < 45) || (315 <= rot && rot < 360) ) {
			return Vector.SIDE_NORTH;
		} else if( 45 <= rot && rot < 135 ) {
			return Vector.SIDE_EAST;
		} else if( 135 <= rot && rot < 225 ) {
			return Vector.SIDE_SOUTH;
		} else if( 225 <= rot && rot < 315 ) {
			return Vector.SIDE_WEST;
		} else {
			throw new RuntimeException("Unknown Direction");
		}
	}
	
	public void update(long currentTick) throws Exception {
		if(x != lastX || y != lastY || z != lastZ || yaw != lastYaw || pitch != lastPitch || bodyYaw != lastBodyYaw) {
			updateMovement();
			lastX = x; lastY = y; lastZ = z; lastYaw = yaw; pitch = lastPitch; lastBodyYaw = bodyYaw;
		}
	}
	
	public abstract boolean checkInside(int x, int y, int z);
	public abstract void updateMovement() throws Exception;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
	
	public int getEID() {
		return EID;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}
	
	public float getBodyYaw() {
		return bodyYaw;
	}
	
	protected abstract boolean isNeedUpdate();
}
