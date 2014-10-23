package darkjet.server.network.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import darkjet.server.network.packets.minecraft.BaseMinecraftPacket;
import darkjet.server.network.packets.minecraft.MinecraftIDs;
import darkjet.server.network.packets.minecraft.MovePlayerPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.ACKPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.NACKPacket;
import darkjet.server.network.packets.raknet.MinecraftDataPacket.InternalDataPacket;
import darkjet.server.network.packets.raknet.RaknetIDs;
import darkjet.server.utility.Utils;

public final class InternalDataPacketQueue {
	private ByteBuffer buffer;
	private ByteBuffer directBuffer;
	private int sequenceNumber = 0;
	private int messageIndex = 0;
	private final int mtu;
	
	protected ArrayList<Integer> ACKQueue; // Received packet queue
	protected ArrayList<Integer> NACKQueue; // Not received packet queue
	protected HashMap<Integer, byte[]> recoveryQueue;
	protected HashMap<Integer, InternalDataPacket> OftenrecoveryQueue;
	
	private final Player owner;
	
	public InternalDataPacketQueue(Player owner, int mtu) {
		this.owner = owner;
		this.mtu = mtu;
		buffer = ByteBuffer.allocate(mtu);
		directBuffer = ByteBuffer.allocate(mtu);
		
		ACKQueue = new ArrayList<Integer>();
		NACKQueue = new ArrayList<Integer>();
		recoveryQueue = new HashMap<Integer, byte[]>();
		OftenrecoveryQueue = new HashMap<Integer, InternalDataPacket>();
		
		resetBuffer();
	}
	
	public final void resetBuffer() {
		buffer.clear();
		buffer.position(4);
	}
	
	public final boolean isEmpty() {
		return buffer.position() == 4;
	}
	
	public final void addVerify(int seq, boolean isNACK) {
		if(isNACK) {
			synchronized (NACKQueue) {
				NACKQueue.add(seq);
			}
		} else {
			synchronized (ACKQueue) {
				ACKQueue.add(seq);
			}
		}
	}
	
	public final void handleVerify(ACKPacket ACK) throws Exception {
		if( ACK.getPID() == RaknetIDs.ACK ) {
			for(int i: ACK.sequenceNumbers){
				recoveryQueue.remove(i);
			}
		} else if( ACK.getPID() == RaknetIDs.NACK ) {
			for(int i: ACK.sequenceNumbers){
				if( recoveryQueue.containsKey(i) ) {
					owner.leader.network.server.sendTo( recoveryQueue.get(i) , owner.IP, owner.port);
				} else if( OftenrecoveryQueue.containsKey(i) ) { //Often Changed Movement Packet!
					InternalDataPacket idp = OftenrecoveryQueue.get(i);
					switch( idp.buffer[0] ) {
						case MinecraftIDs.MOVE_PLAYER:
							MovePlayerPacket mpp = new MovePlayerPacket(owner.getEID(), owner.getX(), owner.getY(), owner.getZ(), owner.getYaw(), owner.getPitch(), owner.getBodyYaw(), false);
							idp.buffer = mpp.getResponse();
							recoverOftenPacket(i, idp.toBinary());
					}
				}
			}
		} else {
			
		}
	}
	
	public final void update() throws Exception {
		synchronized (ACKQueue) {
			if(this.ACKQueue.size() > 0){
				int[] array = new int[this.ACKQueue.size()];
				int offset = 0;
				for(Integer i: ACKQueue){
					array[offset++] = i;
				}
				ACKPacket pck = new ACKPacket();
				pck.sequenceNumbers = array;
				owner.leader.network.server.sendTo( pck.getResponse() , owner.IP, owner.port);
			}
		}
		synchronized (NACKQueue) {
			if(NACKQueue.size() > 0){
				int[] array = new int[NACKQueue.size()];
				int offset = 0;
				for(Integer i: NACKQueue){
					array[offset++] = i;
				}
				NACKPacket pck = new NACKPacket();
				pck.sequenceNumbers = array;
				owner.leader.network.server.sendTo( pck.getResponse() , owner.IP, owner.port);
			}
		}
	}
	
	public final void addMinecraftPacket(BaseMinecraftPacket bmp) throws Exception {
		addMinecraftPacket( bmp.getResponse() );
	}
	
	/**
	 * Add MinecraftPacket to Buffer, if Buffer is Full, send directly
	 * @param buf
	 */
	public final void addMinecraftPacket(byte[] buf) throws Exception {
		synchronized ( this ) {
			if( mtu < buffer.position() + buf.length + 4 ) {
				//Buffer is Empty = buf too big to send.
				if( isEmpty() ) {
					throw new RuntimeException("Unhandled Too Big Packet");
				} else {
					send();
					//retry
					addMinecraftPacket(buf);
				}
				return;
			}
			InternalDataPacket idp = new InternalDataPacket();
			idp.buffer = buf;
			idp.reliability = 2;
			idp.messageIndex = messageIndex++;
			buffer.put( idp.toBinary() );
		}
	}
	
	public final void sendOffenPacket(BaseMinecraftPacket pak) throws Exception {
		synchronized ( this ) {
			InternalDataPacket idp = InternalDataPacket.wrapMCPacket(pak.getResponse(), messageIndex++);
			directBuffer.clear(); directBuffer.position(4);
			directBuffer.put( idp.toBinary() );
			OftenrecoveryQueue.put(sequenceNumber, idp);
			send(directBuffer);
		}
	}
	
	protected final void recoverOftenPacket(int seq, byte[] buf) throws Exception {
		synchronized ( this ) {
			directBuffer.clear(); directBuffer.position(4);
			directBuffer.put( buf );
			send(seq, directBuffer);
		}
	}
	
	public final void send() throws Exception {
		recoveryQueue.put(sequenceNumber, send(buffer));
	}
	
	private final byte[] send(ByteBuffer buffer) throws Exception {
		return send(sequenceNumber++, buffer);
	}
	
	protected final byte[] send(int seq, ByteBuffer buffer) throws Exception {
		synchronized ( this ) {
			//System.out.println("seq:" + seq);
			int len = buffer.position();
			buffer.position(0);
			buffer.put( RaknetIDs.DATA_PACKET_4 );
			buffer.put( Utils.putLTriad(seq) );
			owner.leader.network.server.sendTo(buffer.array(), 4+len, owner.IP, owner.port);
			return Arrays.copyOfRange(buffer.array(), 0, 4+len);
		}
	}
}