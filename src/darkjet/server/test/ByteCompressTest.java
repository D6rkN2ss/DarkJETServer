package darkjet.server.test;

import static org.junit.Assert.*;

import org.junit.Test;

import darkjet.server.Utils;

public class ByteCompressTest {

	@Test
	public void test() throws Exception {
		byte[] t1 = new byte[]{0x00, (byte) 0xff, (byte) 0xbb};
		byte[] t2 = new byte[]{0x40, (byte) 0xff, (byte) 0xab};
		byte[] t = new byte[]{0x00, (byte)0xff, (byte) 0xbb, 0x40, (byte) 0xff, (byte) 0xab};
		
		byte[] c1 = Utils.decompressByte( Utils.compressByte(t) );
		byte[] c2 = Utils.decompressByte( Utils.compressByte(t1, t2) );
		
		assertArrayEquals(c1, c2);
	}

}
