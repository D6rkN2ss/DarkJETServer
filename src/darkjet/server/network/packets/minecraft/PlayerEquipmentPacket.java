package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class PlayerEquipmentPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.PLAYER_EQUIPMENT;
	}
	public int eid;
	public short item, meta;
	public byte slot;
	
	@Override
	public void parse() {
		bb.get();
		eid = bb.getInt();
		item = bb.getShort();
		meta = bb.getShort();
		slot = bb.get();
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(10);
		bb.put( MinecraftIDs.PLAYER_EQUIPMENT );
		bb.putInt(eid);
		bb.putShort(item);
		bb.putShort(meta);
		bb.put(slot);
		
		return bb.array();
	}

}
