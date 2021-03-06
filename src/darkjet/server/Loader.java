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
		BufferedReader scanInput = new BufferedReader( new InputStreamReader(System.in) );
		while ( !leader.exit ) {
			try {
				String input = scanInput.readLine();
				leader.command.handleCommand(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.print(Logger.INFO, "Good bye!");
	}
}
