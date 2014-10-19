package darkjet.server.network.packets.minecraft;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class ChatPacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.CHAT;
	}
	
	public String sender;
	public String message;
	
	public ChatPacket(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	@Override
	public byte[] getResponse() {
		int totalLength = 6+sender.length() + message.length();
		bb = ByteBuffer.allocate(totalLength);
		short senderLength = (short) sender.length();
		short messageLength = (short) message.length();
		bb.put( MinecraftIDs.CHAT );
		bb.putShort(senderLength);
		try{
			bb.put(sender.getBytes("UTF-8"));
			bb.putShort(messageLength);
			bb.put(message.getBytes(Charset.forName("UTF-8")));
		}
		catch(UnsupportedEncodingException e){
			e.printStackTrace();
			throw new RuntimeException("UTF-8 Not Support!?");
		}
		return bb.array();
	}
	
	

}
