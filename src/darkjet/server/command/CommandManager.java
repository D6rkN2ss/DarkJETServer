package darkjet.server.command;

import darkjet.server.Leader;
import darkjet.server.Logger;
import darkjet.server.Leader.BaseManager;
import darkjet.server.network.player.Player;

/**
 * Basic Command Manager
 * @author Blue Electric
 */
public final class CommandManager extends BaseManager {
	public CommandManager(Leader leader) {
		super(leader);
	}

	public final void handleCommand(String Line) {
		if( Line.equals("exit") ) {
			Logger.print(Logger.INFO, "Exiting...");
			leader.stop();
		} else if( Line.equals("status-dev") ) {
			for( Thread thread : Thread.getAllStackTraces().keySet() ) {
				Logger.print( Logger.DEBUG , "Thread %s", thread.getName() );
				for( StackTraceElement ste : thread.getStackTrace() ) {
					Logger.print( Logger.DEBUG , "%s, %s, %d" , ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
				}
			}
		} else if( Line.equals("list") ) {
			for(Player p : leader.player.Players.values() ) {
				Logger.print(Logger.INFO, "%s(%s)", p.name, p.IP);
			}
		} else if( Line.equals("dump") ) {
			Player p = leader.player.getPlayer("192.168.1.3");
			if(p == null) {
				return;
			}
			Logger.print(Logger.INFO, "%d, %d, %d", p.getX(), p.getY(), p.getZ());
		}
	}
	
	public static interface Commander {
		public String getName();
	}

	@Override
	public void Init() {
	}

	@Override
	public void onClose() {
	}
}
