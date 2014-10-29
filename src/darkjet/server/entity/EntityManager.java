package darkjet.server.entity;

import java.util.ArrayList;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

/**
 * Manager of Entity
 * @author Blue Electric
 */
public final class EntityManager extends BaseManager {
	public int entityCount = 1;
	private final ArrayList<Entity> Entites = new ArrayList<>();
	
	public EntityManager(Leader leader) {
		super(leader);
	}
	
	public final int getNewEntityID() {
		return entityCount++;
	}
	
	public final void addEntity(Entity e) {
		synchronized (Entites) {
			Entites.add(e);
		}
	}
	
	public final Entity getInsideEntity(int x, int y, int z) {
		for( Entity e : Entites ) {
			if( e.checkInside(x, y, z) ) {
				return e;
			}
		}
		return null;
	}

	@Override
	public void Init() {
		
	}

	@Override
	public void onClose() {
		
	}

}
