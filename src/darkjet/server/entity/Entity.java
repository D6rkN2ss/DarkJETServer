package darkjet.server.entity;

import darkjet.server.Leader;
import darkjet.server.network.minecraft.MovePlayerPacket;
import darkjet.server.network.player.Player;

public abstract class Entity {
	public final Leader leader;
	protected float x, y, z, yaw, pitch;
	protected float lastX, lastY, lastZ;
	protected int EID;
	
	public Entity(Leader leader, int EID) {
		this.leader = leader;
		this.EID = EID;
	}
	
	public void update() throws Exception {
		if(x != lastX || y != lastY || z != lastZ) {
			if( this instanceof Player ) {
				MovePlayerPacket mpp = new MovePlayerPacket(EID, x, y, z, yaw, pitch, 0F, false);
				leader.player.broadcastPacket(mpp, (Player) this);
			}
		}
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
