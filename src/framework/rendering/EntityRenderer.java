package framework.rendering;

import world.Planet;
import framework.BetaThread;

public class EntityRenderer extends BetaThread{
	private Planet planet;
	private VBO vbo;
	public EntityRenderer(Planet p, VBO v){
		super("Entity Renderer");
		planet = p;
		vbo = v;
	}
	public void run() {
		while(true){
			//planet.renderEntities();
		}
	}
}
