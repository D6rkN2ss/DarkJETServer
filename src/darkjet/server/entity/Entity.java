package darkjet.server.entity;

import darkjet.server.Leader;
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
	
	public Entity(Leader leader, int EID) {
		this.leader = leader;
		this.EID = EID;
		
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
	
	public void close() throws Exception {
		leader.task.removeTask(mt);
	}
	
	public void update() throws Exception {
		if(x != lastX || y != lastY || z != lastZ || yaw != lastYaw || pitch != lastPitch || bodyYaw != lastBodyYaw) {
			updateMovement();
			lastX = x; lastY = y; lastZ = z; lastYaw = yaw; pitch = lastPitch; lastBodyYaw = bodyYaw;
		}
	}
	
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
