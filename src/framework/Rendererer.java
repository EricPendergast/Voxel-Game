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

import world.Chunk;
import world.Planet;
import world.SolarSystem;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
public class Rendererer{
	 public static final int floatSize = 4;
     public static final int vertexSize = 3;
     public static final int colorSize = 3;
     public static final int textureCoordSize = 2;
     public static final int maxPolys = 10000000;
     public static int vboVertexHandle;
     public static int vboColorHandle;
     public static int vboTextureHandle;
     public static int numVerticies = 0;
     public static DisplayMode displayMode;
     TTEmporaryPlayer p;
     
     SolarSystem solarSystem;
     Texture blocks;
	public Rendererer(){
		 try {
//			 Display.setFullscreen(false);
//				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//				displayMode = new DisplayMode((int)(screen.width/1.5),(int)(screen.height/1.4));
//				Display.setDisplayMode(displayMode);
//				Display.setTitle("Game");
//				Display.create();
				createWindow();
				initGL();
				blocks = loadTexture("res/minecraftTemporary.png");
				blocks.bind();
//	            Display.setDisplayMode(new DisplayMode(640, 480));
//	            Display.setTitle("Vertex Buffer Object Demo");
//	            Display.create();
	        } catch (LWJGLException e) {
	            e.printStackTrace();
	            Display.destroy();
	            System.exit(1);
	        }

//	        glMatrixMode(GL_PROJECTION);
//	        glLoadIdentity();
//	        glOrtho(1, -1, 1, -1, 1, -1);
//	        glMatrixMode(GL_MODELVIEW);
//	        glLoadIdentity();
		 
		 	//GL11.glEnable(GL11.GL_TEXTURE_2D);
			//GL11.glShadeModel(GL11.GL_SMOOTH);
			//GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			//GL11.glClearDepth(1.0f);
			//GL11.glEnable(GL11.GL_DEPTH_TEST);
			//GL11.glDepthFunc(GL11.GL_LEQUAL);
			
//			GL11.glMatrixMode(GL11.GL_PROJECTION);
//			GL11.glLoadIdentity();
//			
//			GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 1000000.0f);
//			//glOrtho(1, -1, 1, -1, 1, -1);
//			GL11.glMatrixMode(GL11.GL_MODELVIEW);
//			
//			glLoadIdentity();
//			//GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
	        
	       
	        ///////
//	        FloatBuffer vertexData = BufferUtils.createFloatBuffer(1000 * vertexSize);
//	        vertexData.put(new float[]{0,1,0,1,2,3,0,4,5});
//	        vertexData.flip();
//
//	        FloatBuffer colorData = BufferUtils.createFloatBuffer(1000 * colorSize);
//	        colorData.flip();
//	        
//	        FloatBuffer textureData = BufferUtils.createFloatBuffer(1000 * textureCoordSize);
//	        textureData.flip();
//	        
//	        vboVertexHandle = glGenBuffers();
//	        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
//	        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
//	        glBindBuffer(GL_ARRAY_BUFFER, 0);
//
//	        vboColorHandle = glGenBuffers();
//	        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
//	        glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
//	        glBindBuffer(GL_ARRAY_BUFFER, 0);
//	        
//	        vboTextureHandle = glGenBuffers();
//	        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
//	        glBufferData(GL_ARRAY_BUFFER, textureData, GL_STATIC_DRAW);
//	        glBindBuffer(GL_ARRAY_BUFFER, 0);
		 FloatBuffer vertexData = BufferUtils.createFloatBuffer(maxPolys*vertexSize);
		 //vertexData.put(new float[]{-0100f, -05f, 0, 05f, -05f, 0, 05f, 05f, 0,0,0,0});
		 vertexData.put(new float[maxPolys*vertexSize]);
		 vertexData.flip();
		
		 FloatBuffer colorData = BufferUtils.createFloatBuffer(maxPolys*colorSize);
		 //colorData.put(new float[]{1,1,1,1,1,1,1,1,1,1,1,1});
		 colorData.put(new float[maxPolys*colorSize]);
		 colorData.flip();
		
		 FloatBuffer textureData = BufferUtils.createFloatBuffer(maxPolys*textureCoordSize);
		 //textureData.put(new float[]{0,0,0,1,1,1,1,0});
		 textureData.put(new float[maxPolys*textureCoordSize]);
		 textureData.flip();
		   
		   vboVertexHandle = glGenBuffers();
		   glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		   glBufferData(GL_ARRAY_BUFFER, vertexData, GL_DYNAMIC_DRAW);
		   glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		   vboColorHandle = glGenBuffers();
		   glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		   glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);
		   glBindBuffer(GL_ARRAY_BUFFER, 0);
		   
		   vboTextureHandle = glGenBuffers();
		   glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
		   glBufferData(GL_ARRAY_BUFFER, textureData, GL_DYNAMIC_DRAW);
		   glBindBuffer(GL_ARRAY_BUFFER, 0);
	        ///////
	        
//	        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
//	        FloatBuffer b = BufferUtils.createFloatBuffer(3);
//	        b.put(new float[]{-100,50,0});
//	        b.flip();
//	        glBufferSubData(GL_ARRAY_BUFFER, 0*floatSize, b);
//	        while (!Display.isCloseRequested()) {
//	        	//p.update();
//	        	//p.render();
//	           render();
//
//	            Display.update();
//	            Display.sync(60);
//	        }
	        solarSystem = new SolarSystem(1);
	        solarSystem.init();
		   //System.out.println(numVerticies);
		   //Blocks.addBlock(Blocks.STONE, 0, 0, 0);
		  // Blocks.addBlock(Blocks.STONE, 0, -1, 0);
	        long startTime = System.currentTimeMillis();
			while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				
				try{
					render();
					Display.update();
					Display.sync(60);
					
					if(System.currentTimeMillis() - startTime > 1000/60){
						startTime = System.currentTimeMillis();
						update();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	        
	        glDeleteBuffers(vboVertexHandle);
	        glDeleteBuffers(vboColorHandle);
	        glDeleteBuffers(vboTextureHandle);

	        Display.destroy();
	        System.exit(0);
	}
	
	private void update(){
		p.update();
		solarSystem.update();
	}
	private void render(){

		 glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		 GL11.glLoadIdentity();
		
		 p.render();
		 //planet.render();
		 ///////
//        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
//        glVertexPointer(vertexSize, GL_FLOAT, 0, 0L);
//
//        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
//        glColorPointer(colorSize, GL_FLOAT, 0, 0L);
//        
//        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
//        glTexCoordPointer(textureCoordSize, GL_FLOAT, 0, 0L);
//        
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glEnableClientState(GL_COLOR_ARRAY);
//        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
//        
//        //glTranslatef(0,0,-7);
//        glDrawArrays(GL_QUADS, 0, amountOfVertices);
//        glDisableClientState(GL_COLOR_ARRAY);
//        glDisableClientState(GL_VERTEX_ARRAY);
//        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		   glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
	       glVertexPointer(vertexSize, GL_FLOAT, 0, 0L);
	
	       glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
	       glColorPointer(colorSize, GL_FLOAT, 0, 0L);
	       
	       glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
	       glTexCoordPointer(textureCoordSize, GL_FLOAT, 0, 0L);
	       
	       glEnableClientState(GL_VERTEX_ARRAY);
	       glEnableClientState(GL_COLOR_ARRAY);
	       glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	       glDrawArrays(GL11.GL_QUADS, 0, numVerticies);
	       glDisableClientState(GL_COLOR_ARRAY);
	       glDisableClientState(GL_VERTEX_ARRAY);
	       glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		 ///////
	}
	
	 //private void drawVBO(int vertHandle, int colorHandle){
		
	//}
	
	private void createWindow() throws LWJGLException{
		Display.setFullscreen(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		displayMode = new DisplayMode((int)(screen.width/1.5),(int)(screen.height/1.4));
		Display.setDisplayMode(displayMode);
		Display.setTitle("Game");
		Display.create();
		Mouse.create();
		p = new TTEmporaryPlayer();
	}
	
	private void initGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 50.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		
	}
	private Texture loadTexture(String file){
		try {
			return TextureLoader.getTexture("png", new FileInputStream(new File(file)), GL11.GL_NEAREST);
		}catch (IOException e) {}
		
		return null;
	}
	
	public static void changeDrawDist(float newDist){
		GLU.gluPerspective(45.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 50.0f);
	}
}