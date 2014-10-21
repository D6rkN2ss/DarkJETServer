package darkjet.server.utility;

public final class RunningTime {
	public final long start;
	
	public RunningTime() {
		start = System.currentTimeMillis();
	}
	
	public final int getRunningTimeinMS() {
		return (int) (System.currentTimeMillis() - start);
	} 
}
