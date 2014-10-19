package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

public final class AdventureSettingPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.ADVENTURE_SETTINGS;
	}
	public int flags;
	public AdventureSettingPacket(int flag) {
		flags = flag;
	}

	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(5);
		bb.putInt(flags);
		
		return bb.array();
	}
	
	

}
