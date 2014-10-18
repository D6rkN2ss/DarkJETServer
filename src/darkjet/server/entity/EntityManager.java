package darkjet.server.entity;

import darkjet.server.Leader;
import darkjet.server.Leader.BaseManager;

public final class EntityManager extends BaseManager {
	public int entityCount = 1;
	
	public EntityManager(Leader leader) {
		super(leader);
	}
	
	public final int getNewEntityID() {
		return entityCount++;
	}

	@Override
	public void Init() {
		
	}

	@Override
	public void onClose() {
		
	}

}
