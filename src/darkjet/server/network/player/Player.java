package darkjet.server.network.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import darkjet.server.Leader;
import darkjet.server.Utils;
import darkjet.server.network.minecraft.BaseMinecraftPacket;
import darkjet.server.network.minecraft.ClientConnectPacket;
import darkjet.server.network.minecraft.MinecraftIDs;
import darkjet.server.network.minecraft.PingPacket;
import darkjet.server.network.minecraft.PongPacket;
import darkjet.server.network.minecraft.ServerHandshakePacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.ACKPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.NACKPacket;
import darkjet.server.network.packets.raknet.MinecraftDataPacket;
import darkjet.server.network.packets.raknet.RaknetIDs;
import darkjet.server.network.packets.raknet.MinecraftDataPacket.InternalDataPacket;

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
	
	private int lastSequenceNum = 0;
	
	private ArrayList<Integer> ACKQueue; // Received packet queue
	private ArrayList<Integer> NACKQueue; // Not received packet queue
	private HashMap<Integer, byte[]> recoveryQueue;
	
	public final InternalDataPacketQueue Queue;
	
	public Player(Leader leader, String IP, int port, short mtu, long clientID) {
		this.leader = leader;
		this.IP = IP;
		this.port = port;
		this.mtu = mtu;
		this.clientID = clientID;
		
		ACKQueue = new ArrayList<Integer>();
		NACKQueue = new ArrayList<Integer>();
		recoveryQueue = new HashMap<Integer, byte[]>();
		
		Queue = new InternalDataPacketQueue(mtu);
	}
	
	public final void close() {
		
	}

	public final void close(String reason) {
		
	}
	
	public final void update() throws Exception {
		if(this.ACKQueue.size() > 0){
			int[] array = new int[this.ACKQueue.size()];
			int offset = 0;
			for(Integer i: ACKQueue){
				array[offset++] = i;
			}
			ACKPacket pck = new ACKPacket();
			pck.sequenceNumbers = array;
			leader.network.server.sendTo( pck.getResponse() , IP, port);
		}
		if(NACKQueue.size() > 0){
			int[] array = new int[NACKQueue.size()];
			int offset = 0;
			for(Integer i: NACKQueue){
				array[offset++] = i;
			}
			NACKPacket pck = new NACKPacket();
			pck.sequenceNumbers = array;
			leader.network.server.sendTo( pck.getResponse() , IP, port);
		}
		if( !Queue.isEmpty() ) {
			Queue.send();
		}
	}
	
	public final void handlePacket(MinecraftDataPacket MDP) throws Exception {
		if(MDP.sequenceNumber - this.lastSequenceNum == 1){
			lastSequenceNum = MDP.sequenceNumber;
		}
		else{
			for(int i = this.lastSequenceNum; i < MDP.sequenceNumber; ++i){
				NACKQueue.add(i);
			}
		}
		ACKQueue.add(MDP.sequenceNumber);
		for(InternalDataPacket ipck : MDP.packets){
			switch( ipck.buffer[0] ) {
				case MinecraftIDs.PING:
					PingPacket ping = new PingPacket(); ping.parse(ipck.buffer);
					PongPacket pong = new PongPacket(ping.pingID);
					Queue.addMinecraftPacket(pong);
					break;
				case MinecraftIDs.CLIENT_CONNECT:
					ClientConnectPacket connect = new ClientConnectPacket();
					connect.parse( ipck.buffer );
					ServerHandshakePacket servershake = new ServerHandshakePacket(port, connect.session);
					Queue.addMinecraftPacket(servershake);
					break;
			}
		}
	}
	
	public final void handleVerfiy(AcknowledgePacket ACK) throws Exception {
		if( ACK.getPID() == RaknetIDs.ACK ) {
			for(int i: ACK.sequenceNumbers){
				recoveryQueue.remove(i);
			}
		} else if( ACK.getPID() == RaknetIDs.NACK ) {
			for(int i: ACK.sequenceNumbers){
				leader.network.server.sendTo( recoveryQueue.get(i) , IP, port);
			}
		} else {
			
		}
	}
	
	public final class InternalDataPacketQueue {
		public ByteBuffer buffer;
		public int sequenceNumber = 0;
		public int messageIndex = 0;
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
		
		public final boolean isEmpty() {
			return buffer.position() == 4;
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
		
		public final void send() throws Exception {
			buffer.position(0);
			buffer.put( RaknetIDs.DATA_PACKET_4 );
			buffer.put( Utils.putLTriad(sequenceNumber) );
			recoveryQueue.put(sequenceNumber, buffer.array());
			leader.network.server.sendTo(buffer.array(), IP, port);
			sequenceNumber++;
			reset();
		}
	}
}
