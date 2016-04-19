package world;

import framework.AlphaThread;

public class EntityUpdater extends AlphaThread{
	private Planet planet;
	public EntityUpdater(Planet p){
		super("Entity Updater");
		planet = p;
	}
	protected void iterate() {
		//planet.updateEntities();
		System.out.println("EntityUpdater iterate");
	}
	
}
