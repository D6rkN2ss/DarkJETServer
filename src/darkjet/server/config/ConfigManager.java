package darkjet.server.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import darkjet.server.Leader;
import darkjet.server.Logger;
import darkjet.server.Leader.BaseManager;

public final class ConfigManager extends BaseManager {
	public ConfigManager(Leader leader) {
		super(leader);
		Configs = new HashMap<>();
	}
	public final static HashMap<String, Object> DefaultConfigs;
	static {
		DefaultConfigs = new HashMap<>();
		DefaultConfigs.put("server-ip", "0.0.0.0");
		DefaultConfigs.put("server-port", 19132);
		DefaultConfigs.put("server-name", "DarkJET TS " + Logger.CodeName);
		DefaultConfigs.put("server-welcome", "@name, Welcome to DarkJETServer");
		DefaultConfigs.put("chunk-length", 4);
	}
	public final String getServerIP() {
		return (String) Configs.get("server-ip");
	}
	public final int getServerPort() {
		return (int) Configs.get("server-port");
	}
	public final String getServerName() {
		return (String) Configs.get("server-name");
	}
	public final String getServerWelcomeMessage() {
		return (String) Configs.get("server-welcome");
	}
	
	public final int getChunkLength() {
		return (int) Configs.get("chunk-length");
	}
	
	public final HashMap<String, Object> Configs;

	@Override
	public void Init() {
		try {
			load();
		} catch (Exception e) {
			Logger.print(Logger.ERROR, "Failed to Load Config");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClose() {
		
	}
	
	public final void load() throws Exception {
		File config = getConfigFile();
		if( !config.exists() ) {
			save();
			return;
		}
		BufferedReader br = new BufferedReader( new FileReader( config ) );
		String readed;
		
		while( (readed = br.readLine()) != null ) {
			if( readed.startsWith("#") || readed.trim().equals("") ) { continue; }
			
			int inx = readed.indexOf("=");
			if( inx == -1 ) {
				Logger.print(Logger.WARNING, "Failed to Parse Line: ", readed);
				continue;
			}
			String name = readed.substring(0, inx);
			String svalue = readed.substring(inx+1);
			
			if( DefaultConfigs.containsKey(name) ) {
				Object dc = DefaultConfigs.get(name);
				Object value = null;
				if( dc.getClass() == Integer.class ) {
					try {
						value = Integer.parseInt(svalue);
					} catch (NumberFormatException nfe) {
						Logger.print(Logger.WARNING, "%s can't be converted to number! in name=%s", svalue, name);
						continue;
					}
				} else {
					value = svalue;
				}
				Configs.put(name, value);
			} else {
				Logger.print(Logger.WARNING, "%s not defined in configs! It is saved in memory but is usually useless.", name);
				Configs.put(name, svalue);
			}
		}
		
		for(String name : DefaultConfigs.keySet()) {
			if( !Configs.containsKey(name) ) {
				Logger.print(Logger.VERBOSE, "%s not defined in config, use default", name);
				Configs.put(name, DefaultConfigs.get(name));
			}
		}
		br.close();
	}
	
	public final void save() throws Exception {
		File config = getConfigFile();
		if( !config.exists() ) {
			Logger.print(Logger.INFO, "No server.config file, generate deafult one.");
			for(String name : DefaultConfigs.keySet()) {
				if( !Configs.containsKey(name) ) {
					Configs.put(name, DefaultConfigs.get(name));
				}
			}
		}
		
		BufferedWriter bw = new BufferedWriter( new FileWriter(config) );
		
		for( String s : Configs.keySet() ) {
			bw.write( s + "=" + Configs.get(s).toString() + System.lineSeparator() );
		}
		bw.close();
	}
	
	public final File getConfigFile() {
		return new File("server.config");
	}

}
