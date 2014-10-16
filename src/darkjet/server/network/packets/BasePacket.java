package darkjet.server.network.packets;

import java.nio.ByteBuffer;

/**
 * Base Packet for Extend
 * @author Blue Electric
 */
public abstract class BasePacket {
	/** Internal Buffer */
	protected ByteBuffer bb;
	
	public BasePacket() {
		
	}
	
	/**
	 * Create Packet with Buffer
	 * @param buffer Internal Buffer
	 */
	public BasePacket(byte[] buffer) {
		bb = ByteBuffer.wrap(buffer);
	}
	
	/**
	 * Set Internal Buffer
	 * @param buffer Internal Buffer
	 */
	public final void setBuffer( byte[] buffer ) {
		bb = ByteBuffer.wrap( buffer );
	}
	
	public abstract int getPID();
	
	/**
	 * Parse with Internal Buffer
	 */
	public void parse() {
		throw new RuntimeException("Didn't support parse");
	}
	/**
	 * Parse Buffer and Save data in Packet Class
	 * @param buffer Buffer to Parse
	 */
	public void parse(byte[] buffer) {
		bb = ByteBuffer.wrap( buffer );
		parse();
	}
	
	/**
	 * Make Packet as Byte to send Someone
	 * @return Byte array of This Packet
	 */
	public byte[] getResponse() {
		throw new RuntimeException("Didn't support getResponse");
	}
	
	public final String getString(){
		byte[] text = new byte[bb.getShort()];
		bb.get(text);
		return new String(text);
	}
}
