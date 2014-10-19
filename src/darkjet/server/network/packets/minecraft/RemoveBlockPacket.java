package darkjet.server.network.packets.minecraft;

public final class RemoveBlockPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.REMOVE_BLOCK;
	}
	public int eid, x, z;
	public byte y;

	@Override
	public void parse() {
		bb.get();
		eid = bb.getInt();
		x = bb.getInt();
		z = bb.getInt();
		y = bb.get();
	}
	
	
}
