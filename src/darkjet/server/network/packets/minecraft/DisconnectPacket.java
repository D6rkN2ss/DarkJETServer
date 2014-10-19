package darkjet.server.network.packets.minecraft;

public final class DisconnectPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.DISCONNECT;
	}

	@Override
	public byte[] getResponse() {
		return new byte[]{MinecraftIDs.DISCONNECT};
	}

	
}
