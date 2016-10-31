package framework;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import framework.rendering.BlockRenderer;
import framework.rendering.Renderer;
import world.Chunk;
import world.Planet;
import world.SolarSystem;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
public class Runner{
    public static DisplayMode displayMode;
     
    SolarSystem solarSystem;
    Renderer renderer;
    public static final ThreadManager threads = new ThreadManager();
	public Runner(){
		renderer = new Renderer();
		solarSystem = new SolarSystem(1);
		renderer.setSolarSystem(solarSystem);
	}
	public void start(){
		NanoTimer whole = new NanoTimer();
		int framerate = 30;
		long nanoPerSec = 1000000000;
		
		solarSystem.init();
		threads.start();
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			
			try{
				BlockRenderer.pause = Keyboard.isKeyDown(Keyboard.KEY_F);
				debug = "";
				Timer t = new Timer();
				t.start();
				debug += " start 1:" + t.getTime();
				update();
				debug += " update 2:" + t.getTime();

				if(Keyboard.isKeyDown(Keyboard.KEY_P))
					renderer.render();
				else
					renderer.render();//(int)(nanoPerSec/framerate - whole.getTime())/1000000 - 20);
				debug += " render 3:" + t.getTime();
				
				if(Keyboard.isKeyDown(Keyboard.KEY_M)){
					@SuppressWarnings("unused")
					int randomlinethatdoesntdoanything = -3;
				}
				
				//Renderer.vbo.sendVerticies(20);//(int)(nanoPerSec/framerate - whole.getTime())/1000000 - 20);
				debug += " send verts 4:" + t.getTime();
				Display.update();
				debug += " disp update 5:" + t.getTime();
				Renderer.vbo.sendVerticies(20);//(int)(nanoPerSec/framerate - whole.getTime())/1000000 - 5);
				threads.waitForAlphaThreads();
				threads.resume();
				if(whole.getTime()-10000000 > nanoPerSec/(framerate)){
					System.out.println("Tick: " + (whole.getMillis()-1000/framerate));
					System.out.println("debug : " + debug);
				}
				whole.start();
				Display.sync(framerate);
				
				
				
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		renderer.delete();
		System.exit(0);
	}
	private void update(){
		solarSystem.update();
	}
	public static String debug;
}