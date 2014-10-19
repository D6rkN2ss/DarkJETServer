package darkjet.server.network;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

/**
 * Manage Network stuff
 * @author Blue Electric
 */
public final class NetworkManager extends BaseManager {
	public final UDPServer server;
	private final Worker worker;
	
	public NetworkManager(Leader leader) {
		super(leader);
		server = new UDPServer(this);
		worker = new Worker();
		worker.start();
	}
	
	public final class Worker extends Thread {
		@Override
		public final void run() {
			byte[] buffer = new byte[102400];
			while ( !isInterrupted() ) {
				try {
					server.Receive(buffer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onClose() {
		while( worker.isAlive() ) {
			server.close();
			worker.interrupt();
		}
	}
	@Override
	public void Init() {
		
	}
}
