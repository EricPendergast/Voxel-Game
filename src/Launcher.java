  import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;

import world.CubeNoise;
import world.PerlinNoise;
import world.PerlinNoiseAdvanced;
import world.TestPlanet;
import framework.Generator;
import framework.Ops;
import framework.Runner;
import framework.queues.ConcurrentNode;
import framework.queues.ConcurrentQueue;

public class Launcher{
	public static void main(String[] args) throws LWJGLException{
		//Adding the lwjgl natives
		System.setProperty("org.lwjgl.librarypath", new File("native/linux").getAbsolutePath());

		//for(int i = 0; i < 20; i++){
		//	System.out.println(Generator.getRandomNumber()%100);
		//}
//		float[] up = {0,1,0};
//		float[] right = {1,0,0};
//		float[] c = Ops.rotate(right, up, (float)Math.toRadians(182));
//		System.out.println(Math.toDegrees(Ops.getAngleBetween(right, c)));
		//System.out.println(Ops.getAngleBetween(new float[]{1.00001f, -1,0}, new float[]{-1 ,1,0}));
		
		//System.out.println(Ops.arrToString(Ops.collide(new float[]{.1f, 0f, 0f ,.4f}, new float[]{0,0,0,.5f})));
		
		//Thing thing = new Thing();
		//thing.start();
		
		
		Runner r = new Runner();
		r.start();
		
		
//		PerlinNoiseAdvanced noise = new PerlinNoiseAdvanced(0, 10, 20, (int)System.currentTimeMillis());
//		BufferedImage image = new BufferedImage(400,400, BufferedImage.TYPE_INT_RGB);
//		
//		for(int i = 0; i < 400; i++){
//			for(int j = 0; j < 400; j++){
//				int gen = (int)Math.floor(noise.genHeight(i-200, j-200))*20;
//				Color color;
//				if(gen<=255)
//					color = new Color(gen,gen,gen);
//				else
//					color = Color.MAGENTA;
//				image.setRGB(i, j, color.getRGB());
//			}
//		}
//		
//		try {
//		    File outputfile = new File("C:/Users/Eric/Pictures/Programming/Image.png");
//		    ImageIO.write(image, "png", outputfile);
//		} catch (IOException e) {}
		
		
//		for(int i = 0; i < 10; i++){
//			Generator.seed(i*i);
//			System.out.println(Generator.generate()%10);
//		}
	}
}

//class Thing implements Runnable{
//	public void run() {
//		while(true){
//			System.out.println("hi");
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public void start(){
//		(new Thread(this)).start();
//	}
//}