package framework.rendering;

import framework.BetaThread;
import world.Planet;

public class BlockRenderer extends BetaThread{
	public static boolean pause = false;
	//private Thread thread;
	Planet planet;
	VBO vbo;
	public BlockRenderer(Planet p, VBO v){
		super("Block Renderer");
		planet = p;
		vbo = v;
	}
	public void start(){
		//myThread = new Thread(this);
		myThread.start();
	}
	public void run() {
		while(true){
			planet.sendPolys();
		}
	}
	
}
