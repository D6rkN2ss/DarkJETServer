package darkjet.server.level;

import darkjet.server.Leader;
import darkjet.server.Utils;
import darkjet.server.level.chunk.ChunkProvider;
import java.io.File;

public final class Level {
	private final Leader leader;
	
	public final String Name;
	private ChunkProvider provider;
	
	public Level(Leader leader, String Name) {
		this.leader = leader;
		this.Name = Name;
	}
	public Level(Leader leader, String Name, ChunkProvider provider) {
		this.leader = leader;
		this.Name = Name;
		this.provider = provider;
	}
	
	public final void load() {
		if( !leader.level.isExist(Name) ) { return; }
		File levelDir = leader.level.getLevelPath(Name);
		File Provider = new File( levelDir.getPath(), "provider");
		
		String ProviderName = new String( Utils.FiletoByteArray( Provider ) );
		Class<ChunkProvider> provider = leader.level.Providers.get(ProviderName);
		try {
			this.provider = provider.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unvalid Level Provider");
		}
	}
	
	public final void save() {
		File levelDir = leader.level.getLevelPath(Name);
		File Provider = new File( levelDir.getPath(), "provider");
		
		Utils.WriteByteArraytoFile( provider.getName().getBytes() , Provider);
	}
}
