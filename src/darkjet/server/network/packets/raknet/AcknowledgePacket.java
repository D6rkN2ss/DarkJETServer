package darkjet.server.network.packets.raknet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import darkjet.server.Logger;
import darkjet.server.network.packets.BasePacket;
import darkjet.server.utility.Utils;

public abstract class AcknowledgePacket extends BasePacket {
	public byte[] buffer;
	public int[] sequenceNumbers;

	@Override
	public byte[] getResponse() {
		ByteBuffer bb = ByteBuffer.allocate(512);
		bb.position(3);
		Arrays.sort(sequenceNumbers);
		int count = sequenceNumbers.length;
		int records = 0;

		if(count > 0) {
			int pointer = 1;
			int start = sequenceNumbers[0];
			int last = sequenceNumbers[0];
			while(pointer < count){
				int current = this.sequenceNumbers[pointer++];
				int diff = current - last;
				if(diff == 1){
					last = current;
				}
				else if(diff > 1){ //Forget about duplicated packets (bad queues?)
					if(start == last){
						bb.put((byte) 0x01);
						bb.put(Utils.putLTriad(start));
						start = last = current;
					}
					else{
						bb.put((byte) 0x00);
						bb.put(Utils.putLTriad(start));
						bb.put(Utils.putLTriad(last));
						start = last = current;
					}
					++records;
				}
			}
			if(start == last){
				bb.put((byte) 0x01);
				bb.put(Utils.putLTriad(start));
			}
			else{
				bb.put((byte) 0x00);
				bb.put(Utils.putLTriad(start));
				bb.put(Utils.putLTriad(last));
			}
			++records;
		}
		int length = bb.position();
		bb.position(0);
		bb.put( (byte) getPID() );
		bb.putShort((short) records);
		
		buffer = Arrays.copyOf(bb.array(), length);
		
		return buffer;
	}

	@Override
	public void parse() {
		bb.get();
		int count = bb.getShort();
		
		List<Integer> packets = new ArrayList<Integer>();
		for(int i = 0; i < count && bb.position() < bb.capacity(); ++i){
			byte[] tmp = new byte[6];
			if(bb.get() == 0x00){
				bb.get(tmp);
				int start = Utils.getLTriad(tmp, 0);
				int end = Utils.getLTriad(tmp, 3);
				if((end - start) > 4096){
					end = start + 4096;
				}
				for(int c = start; c <= end; ++c){
					packets.add(c);
				}
			}
			else{
				bb.get(tmp, 0, 3);
				packets.add(Utils.getLTriad(tmp, 0));
			}
		}
		sequenceNumbers = new int[packets.size()];
		for(int i = 0; i < this.sequenceNumbers.length; i++){
			sequenceNumbers[i] = packets.get(i);
		}
	}
	
	public final static class ACKPacket extends AcknowledgePacket { @Override public int getPID() { return RaknetIDs.ACK; } }
	public final static class NACKPacket extends AcknowledgePacket { @Override public int getPID() { return RaknetIDs.NACK; } }
}
