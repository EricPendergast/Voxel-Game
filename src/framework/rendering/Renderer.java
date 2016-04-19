package framework.rendering;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import framework.GUImanager;
import framework.Globals;
import framework.Runner;
import framework.Timer;
import world.SolarSystem;

public class Renderer {
    public static DisplayMode displayMode;
    Shader defaultShader;
    Texture blocks;
    SolarSystem solarSystem;
    public static VBO vbo;
    public static MemoryManager mem;
    GUImanager gui;
    
	public Renderer(){
		try {
			createWindow();
			initGL();
			blocks = loadTexture("res/minecraftTemporary.png");
			blocks.bind();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
		class DShader extends Shader{
			public DShader(StringBuilder vertSource, StringBuilder fragSource) {super(vertSource, fragSource);}
			public void enable(){
				super.enable();
				setUniform("crosshairWidth", Globals.crosshairWidth);
				GL20.glUniform2f(getUniformLocation("crosshairPos"), Globals.crosshairPos[0], Globals.crosshairPos[1]);
			}
		}
		defaultShader = new DShader(Shader.loadFile("res/shaders/shader.vert"),Shader.loadFile("res/shaders/shader.frag"));
		Globals.crosshairWidth = 5;
		Globals.crosshairPos = new int[]{displayMode.getWidth()/2, displayMode.getHeight()/2};
		//defaultShader.setUniform("crosshairWidth", 10);
		//GL20.glUniform1i(defaultShader.getUniformLocation("crosshairWidth"), 10);
		vbo = new VBO();
		mem = new MemoryManager(vbo,24);
		
		gui = new GUImanager(displayMode);
	}
	public void setSolarSystem(SolarSystem s){
		solarSystem = s;
	}
	float i = 0;
	public void render(){
		//debug = "";
		//Timer t = new Timer();
		//t.start();
		//3d projection settings
		////
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		//Runner.debug += " RENDERER1:" + t.getTime();
		GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 10000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		blocks.bind();
		//Runner.debug += " 2:" + t.getTime();
		////
		glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	 	GL11.glLoadIdentity();
	 	//Runner.debug += " 3:" + t.getTime();

		solarSystem.render();
		//Runner.debug += " 4:" + t.getTime();
		
		defaultShader.enable();
		defaultShader.setUniform("i", i);
		if(Keyboard.isKeyDown(Keyboard.KEY_1))
			i -= .05;
		if(Keyboard.isKeyDown(Keyboard.KEY_2))
			i += .05;
		vbo.render();
		//Runner.debug += " 5:" + t.getTime();

	    ////
	    //2d rendering settings
	    //Select The Projection Matrix
	    GL11.glMatrixMode(GL11.GL_PROJECTION); 
	    // Reset The Projection Matrix
	    GL11.glLoadIdentity(); 
	    //Projection for 2d to adress pixel by x,y
	    GL11.glOrtho(0, displayMode.getWidth()/(float)displayMode.getHeight()*100,100,0,-1,1);
	    // Select The Modelview Matrix
	    GL11.glMatrixMode(GL11.GL_MODELVIEW); 
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glLoadIdentity(); 
	    ////
	    //Runner.debug += " 6:" + t.getTime();

	    gui.render();
	    //Runner.debug += " 7:" + t.getTime() + "}";

	}
	private void createWindow() throws LWJGLException{
		Display.setFullscreen(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		displayMode = new DisplayMode((int)(screen.width/1.5),(int)(screen.width/2.8-100));
		Display.setDisplayMode(displayMode);
		Display.setTitle("Game");
		Display.setResizable(true);
		Display.create();
		Mouse.create();
	}
	
	private void initGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0,0,0,0);//89.0f/200.0f, 160.0f/200.0f, 255.0f/255.0f, 1f);
		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CW);
		//enables blending which allows for transparency
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	private Texture loadTexture(String file){
		try {
			return TextureLoader.getTexture("png", new FileInputStream(new File(file)), GL11.GL_NEAREST);
		}catch (IOException e) { 
			System.err.println("Failed to load texture file");
		}
		
		return null;
	}
	
	public static void changeDrawDist(float newDist){
		GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, newDist);
	}
	
	public void delete(){
		vbo.delete();
		defaultShader.delete();
		Display.destroy();
	}
}
