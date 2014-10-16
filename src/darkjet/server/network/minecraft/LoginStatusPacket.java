package darkjet.server.network.minecraft;

import java.nio.ByteBuffer;

public final class LoginStatusPacket extends BaseMinecraftPacket {
	public final static int NORMAL = 0;
	public final static int CLIENT_OUTDATE = 1;
	public final static int SERVER_OUTDATE = 2;
	public int status;
	
	@Override
	public int getPID() {
		return MinecraftIDs.LOGIN_STATUS;
	}

	public LoginStatusPacket(int status){
		this.status = status;
		bb = ByteBuffer.allocate(5);
	}

	@Override
	public byte[] getResponse() {
		bb.put( MinecraftIDs.LOGIN_STATUS );
		bb.putInt(status);
		
		return bb.array();
	}
}
