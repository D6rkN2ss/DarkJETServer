package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import darkjet.server.Utils;
import darkjet.server.network.minecraft.BaseMinecraftPacket;
import darkjet.server.network.packets.BasePacket;
import darkjet.server.network.player.Player;

public final class MinecraftDataPacket extends BasePacket {
	protected int PID;
	public int sequenceNumber;
	public List<InternalDataPacket> packets;
	
	@Override
	public int getPID() {
		return PID;
	}
	
	@Override
	public void parse() {
		PID = bb.get();
		sequenceNumber = Utils.getLTriad( bb.array(), bb.position() );
		bb.position( bb.position()+3 ); 
		
		byte[] data = new byte[bb.capacity() - 4];
		bb.get(data);
		packets = Arrays.asList(InternalDataPacket.fromBinary(data));
	}
	
	public final static class InternalDataPacket {
		public byte[] buffer;
		public byte reliability;
		public boolean hasSplit;
		public int messageIndex = -1;
		public int orderIndex = -1;
		public byte orderChannel = (byte)0xff;
		public int splitCount = -1;
		public short splitID = -1;
		public int splitIndex = -1;
		public int sequenceNumber;

		public static InternalDataPacket[] fromBinary(byte[] buffer){
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			ArrayList<InternalDataPacket> list = new ArrayList<>();
			while(bb.position() < bb.capacity()) {
				InternalDataPacket pck = new InternalDataPacket();
				byte flag = bb.get();
				pck.reliability = (byte) (flag >> 5);
				pck.hasSplit = (flag & 0b00010000) == 16;
				int length = ((bb.getShort() + 7) >> 3); // The Length is in bits, so Bits to Bytes conversion
				if(pck.reliability == 2 || pck.reliability == 3 || pck.reliability == 4 || pck.reliability == 6 || pck.reliability == 7){
					pck.messageIndex = Utils.getLTriad(buffer, bb.position());
					bb.position(bb.position() + 3);
				}
				if(pck.reliability == 1 || pck.reliability == 3 || pck.reliability == 4 || pck.reliability == 7){
					pck.orderIndex = Utils.getLTriad(buffer, bb.position());
					bb.position(bb.position() + 3);
					pck.orderChannel = bb.get();
				}
				if(pck.hasSplit){
					pck.splitCount = bb.getInt();
					pck.splitID = bb.getShort();
					pck.splitIndex = bb.getInt();
				}
				pck.buffer = new byte[length];
				bb.get(pck.buffer);
				list.add(pck);
			}
			InternalDataPacket[] result = new InternalDataPacket[list.size()];
			list.toArray(result);
			return result;
		}

		public int getLength(){
			return 3 + buffer.length + (messageIndex != -1 ? 3:0) + (orderIndex != -1 ? 4:0) +  (hasSplit ? 10:0);
		}

		public byte[] toBinary(){
			ByteBuffer bb = ByteBuffer.allocate(getLength());
			bb.put((byte) ((reliability << 5) ^ (hasSplit ? 0b0001 : 0x00)));
			bb.putShort((short) (buffer.length << 3));
			if(reliability == 0x02 || reliability == 0x03 || reliability == 0x04 || reliability == 0x06 || reliability == 0x07){
				bb.put(Utils.putLTriad(this.messageIndex));
			}
			if(reliability == 0x01 || reliability == 0x03 || reliability == 0x04 || reliability == 0x07){
				bb.put(Utils.putLTriad(this.orderIndex));
				bb.put(this.orderChannel);
			}
			if(hasSplit){
				bb.putInt(splitCount);
				bb.getShort(splitID);
				bb.putInt(splitIndex);
			}
			bb.put(buffer);
			return bb.array();
		}
		
		public final static class InternalDataPacketQueue {
			public ByteBuffer buffer;
			
			public int sequenceNumber;
			
			public final Player player;
			public final int mtu;
			
			public InternalDataPacketQueue(Player player, int mtu) {
				this.player = player;
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
				player.leader.network.server.sendTo(buffer.array(), player.IP, player.port);
			}
		}
	}
}
