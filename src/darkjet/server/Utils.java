package darkjet.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

/**
 * Utility from Block Server
 * @author BlockServer
 */
public class Utils{
	/**
	 * Left-side Int
	 * @param i Int to make Left-side
	 * @return Left-sided Int
	 * @author Blue Electric
	 */
	public final static byte[] LInt(int i) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(i);
		byte[] result = new byte[4];
		result[0] = buffer.array()[3];
		result[1] = buffer.array()[2];
		result[2] = buffer.array()[1];
		result[3] = buffer.array()[0];
		return result;
	}
	
	/**
	 * Delete Folder with Sub-files
	 * @param file Folder to Delete
	 */
	public static void recursiveDelete(File file) {
	    if( !file.exists() ) { return; }
	    if (file.isDirectory()) {
	        for (File f : file.listFiles()) {
		        recursiveDelete(f);
		    }
		}
		file.delete();
	}
	
	/**
	 * File to ByteArray
	 * @param file File to Read
	 * @return File's Buffer
	 * @author Blue Electric
	 */
	public final static byte[] FiletoByteArray(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] result = new byte[ fis.available() ];
			fis.read(result); fis.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Write Bytes to File
	 * @param bs Byte Array to Write
	 * @param file File to Write
	 * @return OK?
	 * @author Blue Electric
	 */
	public final static boolean WriteByteArraytoFile(byte[] bs, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bs); fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Compress byte for Sending Chunk or Etc.
	 * @param compress Bytes to Compress
	 * @return compressed Byte
	 * @author Blue Electric
	 */
	public final static byte[] compressByte(byte[] compress) throws Exception {
		return compressByte( new byte[][]{compress} );
	}
	
	public final static byte[] compressByte(byte[]... compress) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream( bos );
		for(byte[] ba : compress) {
			dos.write(ba);
		}
		dos.close();
		byte[] buf = bos.toByteArray();
		bos.close();
		return buf;
	}
	
	public final static byte[] decompressByte( byte[] decompress ) throws Exception {
		return decompressByte(decompress, 0);
	}
	public final static byte[] decompressByte( byte[] decompress, int startInx ) throws Exception {
		Inflater inflater = new Inflater();
		inflater.setInput(decompress, startInx, decompress.length-startInx);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		while( !inflater.finished() ) {
			int count = inflater.inflate(buf);
			bos.write(buf, 0, count);
		}
		inflater.end();
		buf = bos.toByteArray();
		bos.close();
		return buf;
	}
	
	public static int getTriad(byte[] data, int offset){
		return (int) (data[offset++] << 16 | data[offset++] << 8  | data[offset]);
	}
	public static int getTriad(ByteBuffer bb){
		return (int) (bb.get() << 16 | bb.get() << 8 | bb.get());
	}
	public static int getLTriad(byte[] data, int offset){
		return (data[offset] & 0xff) | (data[offset+1] & 0xff) << 8 | (data[offset+2] & 0xff) << 16;
	}

	public static byte[] putTriad(int v){
		return put(v, 3, false);
	}
	public static byte[] putLTriad(int v){
		return put(v, 3, true);
	}
	public static byte[] put(int x, int len, boolean reverse){
		byte[] buffer = new byte[len];
		int shift = (len - 1) * 8;
		for(int i = 0; i < len; i++){
			buffer[reverse ? (len - i - 1):i] = (byte) ( x >> shift);
			shift -= 8;
		}
		return buffer;
	}

	public static <T> T[] arrayShift(T[] t, T[] emptyBuffer){
		System.arraycopy(t, 1, emptyBuffer, 0, t.length - 1);
		return emptyBuffer;
	}
	public static <T> T arrayShift(List<T> t){
		return t.remove(0);
	}
	public static <T> T arrayRandom(T[] array){
		return arrayRandom(array, new Random());
	}
	public static <T> T arrayRandom(T[] array, Random random){
		return array[random.nextInt(array.length)];
	}
	public static <T> T[] toArray(Collection<T> coll, Class<T> clazz){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(clazz, coll.size());
		int i = 0;
		for(T item: coll){
			arr[i++] = item;
		}
		return arr;
	}

	public static void setNibble(byte x, byte y, byte z, byte nibble, byte[] buffer){
		int offset = (x << 7) + (z << 3) + (y >> 1);
		byte b = buffer[offset];
		if((y & 1) == 0){
			b &= 0xF0;
			b |= (nibble & 0x0F);
		}
		else{
			b &= 0x0F;
			b |= ((nibble << 4) & 0xF0);
		}
		buffer[offset] = b;
	}
	public static byte getNibble(byte x, byte y, byte z, byte[] buffer){
		return (byte) (0x0F & (buffer[(x << 7) + (z << 3) + (y >> 1)] >> ((y & 1) == 0 ? 0:4)));
	}
}
