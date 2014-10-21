package darkjet.server.level.chunk.provider;

import java.io.File;
import java.util.Stack;
import java.util.zip.Inflater;

import com.sun.org.apache.bcel.internal.classfile.InnerClass;

import darkjet.server.Utils;
import darkjet.server.level.Level;
import darkjet.server.level.chunk.Chunk;
import darkjet.server.level.chunk.ChunkGenerator;
import darkjet.server.level.chunk.ChunkProvider;

/**
 * Basic Chunk Provider for DarkJET Server
 * @author Blue Electric
 */
public class BasicChunkProvider extends ChunkProvider {
	public final Worker worker;
	
	public final Stack<BasicChunk> decompressStack = new Stack<>();
	
	public BasicChunkProvider(Level level, ChunkGenerator generator) {
		super(level, generator);
		
		worker = new Worker();
		worker.start();
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		if( !isGenerated(x, z) )
		{ return null; }
		BasicChunk chunk = new BasicChunk(x, z);
		chunk.compressBuffer = Utils.FiletoByteArray( getChunkFile(x, z) );
		decompressStack.push(chunk);
		return chunk;
	}

	@Override
	public boolean saveChunk(Chunk chunk) {
		synchronized (chunk) {
			BasicChunk bc = (BasicChunk) chunk;
			Utils.WriteByteArraytoFile(bc.getCompress(true), getChunkFile(chunk.x, chunk.z));
			return true;
		}
	}

	@Override
	public String getName() {
		return "Basic";
	}

	@Override
	public boolean isGenerated(int x, int z) {
		return getChunkFile(x, z).exists();
	}
	
	public final File getChunkFile(int x, int z) {
		return new File(ChunkDir, x + "_" + z);
	}

	@Override
	public Chunk getEmptyChunk(int x, int z) {
		return new BasicChunk(x, z, true);
	}
	
	public final class Worker extends Thread {
		@Override
		public final void run() {
			while( !isInterrupted() ) {
				while( !decompressStack.isEmpty() && !isInterrupted() ) {
					BasicChunk chunk = decompressStack.pop();
					Inflater inflater = new Inflater();
					inflater.setInput(chunk.compressBuffer);
					try {
						//x, y
						inflater.inflate( new byte[8] );
						inflater.inflate( chunk.blockIDs );
						inflater.inflate( chunk.blockDamages );
						inflater.inflate( chunk.skyLights );
						inflater.inflate( chunk.blockLights );
						inflater.inflate( chunk.biomeIds );
						inflater.inflate( chunk.biomeColors );
						chunk.touched = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try { Thread.sleep(50); } catch (InterruptedException e) { }
			}
		}
	}

	@Override
	public void onClose() {
		while( worker.isAlive() ) {
			worker.interrupt();
		}
	}
	
	public final static class BasicChunk extends Chunk {
		protected boolean touched = false; //touched by Provider
		protected byte[] compressBuffer;
		public boolean JustGenerated;
		
		public BasicChunk(int x, int z) {
			this(x, z, false);
		}
		
		public BasicChunk(int x, int z, boolean JustGenerated) {
			super(x, z);
			this.touched = true;
			this.JustGenerated = JustGenerated;
		}
		
		@Override
		public byte[] getCompressed() {
			return getCompress(false);
		}
		
		public byte[] getCompress(boolean forcely) {
			if( (touched && wasModify) || JustGenerated || forcely) {
				compressBuffer = super.getCompressed();
				JustGenerated = false; wasModify = false;
			}
			return compressBuffer;
		}
		
		@Override
		public boolean isReady() {
			return touched;
		}
	}
	
	/*
	 * Basic Chunk Provider for DarkJET Server
	 * Release 0.39 : 느리지만 작동만 된다면 상관없잖아?
	 */
}
