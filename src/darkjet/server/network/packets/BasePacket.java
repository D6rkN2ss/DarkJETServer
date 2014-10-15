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
	
	/**
	 * Parse with Internal Buffer
	 */
	public final void parse() {
		parse(bb.array());
	}
	/**
	 * Parse Buffer and Save data in Packet Class
	 * @param buffer Buffer to Parse
	 */
	public void parse(byte[] buffer) {
		throw new RuntimeException("Didn't support parse");
	}
	
	/**
	 * Make Packet as Byte to send Someone
	 * @return Byte array of This Packet
	 */
	public byte[] getResponse() {
		throw new RuntimeException("Didn't support getResponse");
	}
}
