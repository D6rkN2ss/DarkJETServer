package darkjet.server.network.player;

import java.nio.ByteBuffer;

import darkjet.server.Leader;
import darkjet.server.network.minecraft.BaseMinecraftPacket;

/**
 * Minecraft Packet Handler
 * @author Blue Electric
 */
public final class Player {
	public final Leader leader;
	public final String IP;
	public final int port;
	public final short mtu;
	public final long clientID;
	
	public final InternalDataPacketQueue Queue;
	
	public Player(Leader leader, String IP, int port, short mtu, long clientID) {
		this.leader = leader;
		this.IP = IP;
		this.port = port;
		this.mtu = mtu;
		this.clientID = clientID;
		
		Queue = new InternalDataPacketQueue(mtu);
	}
	
	public final void close() {
		
	}

	public final void close(String reason) {
		
	}
	
	public final class InternalDataPacketQueue {
		public ByteBuffer buffer;
		
		public int sequenceNumber;
		
		public final int mtu;
		
		public InternalDataPacketQueue(int mtu) {
			this.mtu = mtu;
			buffer = ByteBuffer.allocate(mtu);
			reset();
		}
		
		public final void reset() {
			buffer.clear();
			buffer.position(4);
		}
		
		public final void addMinecraftPacket(BaseMinecraftPacket bmp) throws Exception {
			addMinecraftPacket( bmp.getResponse() );
		}
		
		/**
		 * Add MinecraftPacket to Buffer, if Buffer is Full, send directly
		 * @param buf
		 */
		public final void addMinecraftPacket(byte[] buf) throws Exception {
			if( buffer.position() < buffer.position() + buf.length ) {
				//Buffer is Empty = buf too big to send.
				if(buffer.position() == 4) {
					throw new RuntimeException("Unhandled Too Big Packet");
				} else {
					send();
					reset();
					//retry
					addMinecraftPacket(buf);
				}
				return;
			}
		}
		
		public final void send() throws Exception {
			leader.network.server.sendTo(buffer.array(), IP, port);
		}
	}
}
