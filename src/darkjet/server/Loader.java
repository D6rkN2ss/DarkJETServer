package darkjet.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.print(Logger.INFO, "Good bye!");
	}
}
