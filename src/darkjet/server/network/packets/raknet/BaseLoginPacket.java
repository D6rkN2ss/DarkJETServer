package darkjet.server.network.packets.raknet;

import darkjet.server.network.packets.BasePacket;

public abstract class BaseLoginPacket extends BasePacket {
	protected int PID;
	
	public BaseLoginPacket() {}
	
	public BaseLoginPacket(byte[] buffer) {
		super(buffer);
	}

	public static final byte[] magic = new byte[] {
		(byte)0x00, (byte)0xff, (byte)0xff, (byte)0x00,
		(byte)0xfe, (byte)0xfe, (byte)0xfe, (byte)0xfe,
		(byte)0xfd, (byte)0xfd, (byte)0xfd, (byte)0xfd,
		(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 };
}
