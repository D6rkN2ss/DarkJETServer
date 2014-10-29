package darkjet.server.level.chunk;

import darkjet.server.utility.Utils;

/**
 * Level <-> Chunk <-> ChunkProvider<br>
 * Interface
 * @author Blue Electric
 */
public class Chunk {
	protected boolean wasModify = false;
	//private boolean saveModify = false;
	protected boolean needSave = false;
	public final byte[] blockIDs;
	public final byte[] blockDamages;
	public final byte[] skyLights;
	public final byte[] blockLights;
	public final byte[] biomeIds;
	public final byte[] biomeColors;
	
	public final int x, z;
	
	public Chunk(int x, int z) {
		this.x = x; this.z = z;
		blockIDs = new byte[0x8000];
		blockDamages = new byte[0x4000];
		skyLights = new byte[0x4000];
		blockLights = new byte[0x4000];
		biomeIds = new byte[0x100];
		biomeColors = new byte[0x400];
	}
	
	public final void setBlock(int x, byte y, int z, byte id, byte meta) {
		int inx = (x << 11) + (z << 7) + y;
		blockIDs[inx] = id;
		
		float fi = (float) inx / 2;
		int minx = (int) Math.floor( fi );
		//0x0000meta
		byte nmeta = (byte) (meta & 0x0f); //cutout first 4 bit
		if( fi != (float) minx ) { //Nibble in Forward
			// 0xmeta0000 | 0x0000orig = 0xmetaorig
			blockDamages[minx] = (byte) ( (nmeta << 4) | ( blockDamages[minx] & 0x0f ) );
		} else { //Nibble in Back
			// 0xorig0000 | 0x0000meta = 0xorigmeta
			blockDamages[minx] = (byte) ( ( blockDamages[minx] & 0xf0 ) | nmeta );
		}
		wasModify = true;
	}
	
	public final byte getBlock(int x, int y, int z) {
		return blockIDs[(x << 11) + (z << 7) + y];
	}
	
	public final byte getBlockMeta(int x, int y, int z) {
		int inx = (x << 11) + (z << 7) + y;
		float fi = (float) inx / 2;
		int minx = (int) Math.floor( fi );
		
		byte damage = blockDamages[minx];
		if( fi != (float) minx ) { //Nibble in Forward
			damage = (byte) (damage >> 4);
		}
		
		return damage;
	}
	
	/**
	 * get Compressed Data for Send
	 * @return Compressed Data
	 */
	public byte[] getCompressed() {
		try {
			return Utils.compressByte( Utils.LInt(x), Utils.LInt(z), blockIDs, blockDamages, skyLights, blockLights, biomeIds, biomeColors );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return This chunk is Ready?
	 */
	public boolean isReady() {
		return true;
	}
}
