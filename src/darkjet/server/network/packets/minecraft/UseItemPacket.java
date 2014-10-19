package darkjet.server.network.packets.minecraft;

public final class UseItemPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.USE_ITEM;
	}

	public int x,y,z,face,eid;
	public short item, meta;
	public float fx, fy, fz, posX, posY, posZ;
	
	@Override
	public void parse() {
		bb.get();
		x = bb.getInt();
		y = bb.getInt();
		z = bb.getInt();
		face = bb.getInt();
		item = bb.getShort();
		meta = bb.getShort();
		eid = bb.getInt();
		fx = bb.getFloat();
		fy = bb.getFloat();
		fz = bb.getFloat();
		posX = bb.getFloat();
		posY = bb.getFloat();
		posZ = bb.getFloat();
	}
	
}
