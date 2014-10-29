package darkjet.server.tasker;

import java.lang.reflect.Method;

/**
 * Task with Object's Method
 * @author Blue Electric
 */
public final class MethodTask extends Task {
	private final Object owner;
	private Method callback;
	
	public MethodTask(int tick, int delay, Object o, String name) throws Exception {
		super(tick, delay);
		this.owner = o;
		this.callback = o.getClass().getMethod(name, new Class<?>[]{long.class});
	}
	@Override
	public final void onRun(long currentTick) {
		try {
			callback.invoke(owner, new Object[]{currentTick} );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onFinish() {
		
	}
}
