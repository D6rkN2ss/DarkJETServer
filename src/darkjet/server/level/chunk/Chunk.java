package darkjet.server.level.chunk;

import darkjet.server.Utils;

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
	 * @return This chunk able to edit?
	 */
	public boolean isEditable() {
		return true;
	}
}
