package darkjet.server.tasker;

/**
 * Do it!
 * @author Blue Electric
 */
public abstract class Task {
	public final int etick, sdelay;
	public int tick, delay;
	
	/**
	 * Task!
	 * @param tick Ticks to End(-1 = Infinite)
	 * @param delay Delay for Each Run
	 */
	public Task(int tick, int delay) {
		this.tick = 0;
		this.etick = tick;
		this.delay = this.sdelay = delay;
	}
	public abstract void onRun();
	public abstract void onFinish();
}
