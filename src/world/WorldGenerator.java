package world;

import framework.BetaThread;

//This class is a thread that has a planet and generates its blocks for it
public class WorldGenerator extends BetaThread{
	Planet planet;
	public WorldGenerator(Planet p){
		super("World Generator");
		planet = p;
	}
	public void run(){
		while(true){
			//try{
				planet.chunkManager.generate();
			//}catch(InterruptedException e){
			//	try {
			//		Thread.sleep(10000);
			//	} catch (InterruptedException f) {}
			//}
//			if(planet.chunkManager.generate()){//if finished generating, pause for 1 second
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {}
//			}
		}
	}
	
	public void start(){
		myThread.start();
	}
	
}
