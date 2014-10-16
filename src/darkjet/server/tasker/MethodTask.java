package darkjet.server.tasker;

import java.lang.reflect.Method;

public final class MethodTask extends Task {
	private final Object owner;
	private Method callback;
	
	public MethodTask(int tick, int delay, Object o, String name) throws Exception {
		super(tick, delay);
		this.owner = o;
		this.callback = o.getClass().getMethod(name, new Class<?>[]{});
	}
	@Override
	public final void onRun() {
		try {
			callback.invoke(owner, new Object[]{} );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onFinish() {
		
	}
}
