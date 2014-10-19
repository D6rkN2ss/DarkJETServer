package darkjet.server.network.packets.minecraft;

import java.nio.ByteBuffer;

import darkjet.server.math.Vector;

public final class StartGamePacket extends BaseMinecraftPacket {
	@Override
	public int getPID() {
		return MinecraftIDs.START_GAME;
	}

	public Vector spawnpos;
	public Vector playerpos;
	
	public long seed;
	public int generator;
	public int gamemode;
	public int eid;
	
	public StartGamePacket(Vector spawnpos, Vector playerpos, int gamemode, long seed, int eid){
		this.spawnpos = spawnpos;
		this.playerpos = playerpos;
		
		this.gamemode = gamemode;
		this.seed = seed;
		this.eid = eid;
		this.generator = 0x1; //0 old, 1 infinite, 2 flat
	}

	@Override
	public byte[] getResponse(){
		bb = ByteBuffer.allocate( 1 + (0x04 * 11) ); //Not sure about this, I think its right
		bb.put(MinecraftIDs.START_GAME); 
		bb.putInt((int) seed);
		bb.put( new byte[]{0x00, 0x00, 0x00, 0x01} );
		bb.putInt(gamemode);
		bb.putInt(eid);
		bb.putInt( spawnpos.getX() );
		bb.putInt( spawnpos.getY() );
		bb.putInt( spawnpos.getZ() );
		bb.putFloat( (float) playerpos.getX() );
		bb.putFloat( (float) playerpos.getY() );
		bb.putFloat( (float) playerpos.getZ() );
		
		return bb.array();
	}
}
