package darkjet.server.entity;

import darkjet.server.Leader;
import darkjet.server.network.packets.minecraft.MovePlayerPacket;
import darkjet.server.network.player.Player;
import darkjet.server.tasker.MethodTask;

public abstract class Entity {
	public final Leader leader;
	protected float x, y, z, yaw, pitch, bodyYaw;
	protected float lastX, lastY, lastZ, lastYaw, lastPitch, lastBodyYaw;
	protected int EID;
	
	public Entity(Leader leader, int EID) {
		this.leader = leader;
		this.EID = EID;
		
		try {
			leader.task.addTask( new MethodTask(-1, 1, this, "update") );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() throws Exception {
		if(x != lastX || y != lastY || z != lastZ || yaw != lastYaw || pitch != lastPitch || bodyYaw != lastBodyYaw) {
			if( this instanceof Player ) {
				updateMovement();
			}
			lastX = x; lastY = y; lastZ = z; lastYaw = yaw; pitch = lastPitch; lastBodyYaw = bodyYaw;
		}
	}
	
	public void updateMovement() throws Exception {
		MovePlayerPacket mpp = new MovePlayerPacket(EID, x, y, z, yaw, pitch, bodyYaw, false);
		leader.player.broadcastPacket(mpp, true, (Player) this);
	}

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
}
