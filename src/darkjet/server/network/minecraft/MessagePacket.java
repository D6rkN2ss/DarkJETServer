package darkjet.server.network.minecraft;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class MessagePacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.MESSAGE;
	}
	
	private byte[] src;
	private byte[] msg;
	
	public MessagePacket() {
		
	}
	public MessagePacket(String msg){
		this("", msg);
	}
	public MessagePacket(String source, String message){
		if(message.length() > 0xFFFF){
			throw new RuntimeException();
		}
		try{
			msg = message.getBytes("UTF-8");
			src = source.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
	}
	@Override
	public void parse() {
		bb.get();
		short senderLength = bb.getShort();
		src = new byte[senderLength];
		bb.get(src);
		short messageLength = bb.getShort();
		msg = new byte[messageLength];
		bb.get(msg);
	}
	@Override
	public byte[] getResponse() {
		bb = ByteBuffer.allocate(6 + msg.length + src.length);
		bb.put( MinecraftIDs.MESSAGE );
		bb.putShort((short) src.length);
		bb.put(src);
		bb.putShort((short) msg.length);
		bb.put(msg);
		
		return bb.array();
	}
	
	public final String getSource() throws UnsupportedEncodingException {
		return new String(src, "UTF-8");
	}
	
	public final String getMessage() throws UnsupportedEncodingException {
		return new String(msg, "UTF-8");
	}
	
}
