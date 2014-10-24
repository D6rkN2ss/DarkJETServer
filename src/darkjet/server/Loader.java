package darkjet.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import darkjet.server.network.player.Player;

/**
 * General load point of DarkJET Server
 * @author Blue Electric
 */
public final class Loader {
	public final static void main(String[] args) {
		Leader leader = new Leader();
		boolean exit = true;
		BufferedReader scanInput = new BufferedReader( new InputStreamReader(System.in) );
		while ( exit ) {
			try {
				String input = scanInput.readLine();
				if( input.equals("exit") ) {
					Logger.print(Logger.INFO, "Exiting...");
					leader.stop();
					exit = false;
				} else if( input.equals("status-dev") ) {
					for( Thread thread : Thread.getAllStackTraces().keySet() ) {
						Logger.print( Logger.DEBUG , "Thread %s", thread.getName() );
						for( StackTraceElement ste : thread.getStackTrace() ) {
							Logger.print( Logger.DEBUG , "%s, %s, %d" , ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
						}
					}
				} else if( input.equals("list") ) {
					for(Player p : leader.player.Players.values() ) {
						Logger.print(Logger.INFO, "%s(%s)", p.name, p.IP);
					}
				} else if( input.equals("dump") ) {
					Player p = leader.player.getPlayer("192.168.1.3");
					if(p == null) {
						break;
					}
					Logger.print(Logger.INFO, "%d, %d, %d", p.getX(), p.getY(), p.getZ());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.print(Logger.INFO, "Good bye!");
	}
}
