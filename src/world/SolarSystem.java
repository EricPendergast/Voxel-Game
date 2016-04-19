package world;

import org.lwjgl.input.Keyboard;

import framework.AlphaThread;
import framework.NanoTimer;
import framework.Runner;
import framework.Timer;
import framework.rendering.BlockRenderer;
import framework.rendering.EntityRenderer;
import framework.rendering.Renderer;

public class SolarSystem{
	private Planet[] planets;
	private Player player;
	private WorldGenerator worldGenerator;
	private BlockRenderer blockRenderer;
	private EntityUpdater entityUpdater;
	private EntityRenderer entityRenderer;
	public SolarSystem(int size){
		planets = new Planet[size];
		player = new Player();
	}
	public void init(){
		planets[0] = new Planet(3, 100, .018f);
		worldGenerator = new WorldGenerator(planets[0]);
		worldGenerator.start();
		Runner.threads.addBetaThread(worldGenerator);
		
		blockRenderer = new BlockRenderer(planets[0],Renderer.vbo);
		blockRenderer.start();
		Runner.threads.addBetaThread(blockRenderer);
		
		entityUpdater = new EntityUpdater(planets[0]);
		entityUpdater.start();
		Runner.threads.addAlphaThread(entityUpdater);
		
//		entityRenderer = new EntityRenderer(planets[0], Renderer.vbo);
//		entityRenderer.start();
//		Runner.threads.addBetaThread(entityRenderer);
		
//		Debug t = new Debug();
//		t.start();
//		Runner.threads.addAlphaThread(t);
	}
	
	public void update(){
		
		planets[0].update(player);
//		if(Keyboard.isKeyDown(Keyboard.KEY_0))
//			System.out.println("Solar system " + planets[0].getChunk(new int[]{0,0,0,Chunk.chunk}));
//		//else
//		//	System.out.println("Solar system " + planets[0].getBlock(1,8,-1));
	}
	public void render(){
		player.render(planets[0]);
		
		//planets[0].sendPolys(timeLeft);
	}
	
	
	public int getSize(){
		return planets.length;
	}
}


//class Debug extends AlphaThread{
//	@Override
//	protected void iterate() {
//		Timer t = new Timer();
//		t.start();
//		while(true){
//			if(t.getTime() > 1000)
//				return;
//		}
//	} 
//	
//	public void resume(){
//		super.resume();
//	}
//}