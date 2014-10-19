package darkjet.server.level;

import java.io.File;
import java.util.HashMap;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;
import darkjet.server.Utils;
import darkjet.server.level.chunk.ChunkGenerator;
import darkjet.server.level.chunk.ChunkProvider;
import darkjet.server.level.chunk.generator.FlatChunkGenerator;
import darkjet.server.level.chunk.provider.BasicChunkProvider;

/**
 * Manager of Level
 * @author Blue Electric
 */
public final class LevelManager extends BaseManager {
	//<Name, Player>
	private HashMap<String, Level> Levels = new HashMap<>();
	public Class<?> DefaultProvider = null;
	public HashMap<String, Class<?>> Providers = new HashMap<>();
	
	public static final File levelFolder = new File(".", "level");
	
	public LevelManager(Leader leader) {
		super(leader);
		
		DefaultProvider = BasicChunkProvider.class;
		Providers.put("Basic", BasicChunkProvider.class);
	}
	
	@Override
	public final void Init() {
		if( !isExist("world") ) {
			createLevel("world");
		} else {
			loadLevel("world");
		}
	}

	/**
	 * Create Level with Default Provider
	 * @param Name Level Name
	 * @return Is Succeed?
	 */
	public final boolean createLevel(String Name) {
		if( isExist(Name) ) { return false; }
		try {
			Level level = new Level(leader, Name);
			level.provider = (ChunkProvider) DefaultProvider.getDeclaredConstructor(Level.class, ChunkGenerator.class).newInstance(level, getDefaultGenerator() );
			level.save();
			Levels.put(Name, level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public final ChunkGenerator getDefaultGenerator() {
		return new FlatChunkGenerator();
	}
	
	/**
	 * Load Level
	 * @param Name Level Name
	 * @return Is Succeed?
	 */
	public final boolean loadLevel(String Name) {
		if( isLoaded(Name) ) { return false; }
		Level level = new Level(leader, Name);
		try {
			level.load();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Levels.put(Name, level);
		return true;
	}
	
	/**
	 * Level is Loaded?
	 * @param Name Level Name
	 * @return Is Succeed?
	 */
	public final boolean isLoaded(String Name) {
		return Levels.containsKey(Name);
	}
	
	public final boolean isExist(String Name) {
		return getLevelPath(Name).isDirectory();
	}
	
	public final Level getLoadedLevel(String Name) {
		if( !isLoaded(Name) ) { return null; }
		return Levels.get(Name);
	}
	
	/**
	 * Get Level Position
	 * @param Name Level Name
	 * @return File of Level Path
	 */
	public final File getLevelPath(String Name) {
		return new File( levelFolder.getPath(), Name);
	}
	
	/**
	 * Remove Level from Disk
	 * @param Name Level Name
	 * @return Is Succeed?
	 */
	public final boolean removeLevel(String Name) {
		unloadLevel(Name);
		File file = getLevelPath(Name);
		if( !file.isDirectory() ) { return false; }
		try {
			Utils.recursiveDelete(levelFolder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Unload Level from Memory
	 * @param Name Level Name
	 * @return Is Succeed?
	 */
	public final boolean unloadLevel(String Name) {
		Level level = Levels.remove(Name);
		if(level == null) { return false; }
		return true;
	}
	
	@Override
	public void onClose() {
		for(Level BL : Levels.values() ) {
			BL.onClose();
		}
	}
}
