package framework;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import world.blocks.*;
import static org.lwjgl.opengl.GL15.*;

public class Blocks {
	public static final int TOP = 0;
	public static final int SOUTH = 1;
	public static final int NORTH = 2;
	public static final int WEST = 3;
	public static final int EAST = 4;
	public static final int BOTTOM = 5;
	
	public static final byte UNGENERATED = 2;
	public static final byte AIR = 0;
	public static final byte STONE = 1;
	
	public static final Block[] list;
	
	static{
		list = new Block[16*16];
		list[0] = new Air();
		for(int i = 1; i < 16*16; i++){
			list[i] = new TemporaryBlockc(i);
		}
		list[UNGENERATED] = new Ungenerated();
	}
	public static byte getOppositeFace(byte face){
		switch(face){
		case TOP:
			return BOTTOM;
		case BOTTOM:
			return TOP;
		case SOUTH:
			return NORTH;
		case NORTH:
			return SOUTH;
		case WEST:
			return EAST;
		case EAST:
			return WEST;
		default:
			return face;
		}
	}
	//no longer used
	public static void renderBlock(byte type, int x, int y, int z){
		if(type == UNGENERATED || type == AIR)
			return;
		
		GL11.glTranslatef((float)x, (float)y, (float)z);
		
		GL11.glBegin(GL11.GL_QUADS);
		//System.out.println(x + " " + y + " " + z);
		
		for(byte i = 0; i < 6; i++){
			drawFace(i,type);
		}
		
	    GL11.glEnd();
	    GL11.glTranslatef((float)-x, (float)-y, (float)-z);
	}
	//no longer used
	private static void drawFace(byte faceNum, byte blockType){//, int x, int y, int z){
		
		float a = (float)(.0625 * (blockType%16));
		float b = (float)(.0625 * (blockType/16));
		
		if(faceNum == 0){//top
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, .5f, .5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, .5f, .5f); 
		}else if(faceNum == 1){//south
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, .5f,.5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, .5f, .5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, .5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, .5f); 
		}else if(faceNum == 2){//north
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( .5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(-.5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(-.5f, -.5f, -.5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( .5f, -.5f, -.5f); 
		}else if(faceNum == 3){//west
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(-.5f, .5f,.5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(-.5f, -.5f, .5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, -.5f); 
		}else if(faceNum == 4){//east
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( .5f, .5f, .5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, .5f,-.5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, -.5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( .5f, -.5f, .5f); 
		}else if(faceNum == 5){//bottom
			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, -.5f, .5f);        
	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, -.5f, .5f);        
	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, -.5f);
	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, -.5f); 
		}
	}
//	public static void addBlock(byte type, int x, int y, int z){
//		if(type == UNGENERATED || type == AIR)
//			return;
//		for(byte i = 0; i < 6; i++){
//			Renderer.numVerticies = addFace(i, (byte)(type-0), x, y, z, Renderer.numVerticies);
//		}
//	}
	
//	public static int addFace(byte faceNum, byte blockType, int x, int y, int z, int start){
//		if(blockType == UNGENERATED || blockType == AIR)
//			return start;
//		float a = (float)(.0625 * (blockType%16));
//		float b = (float)(.0625 * (blockType/16));
//		FloatBuffer textures = BufferUtils.createFloatBuffer(8);
//		FloatBuffer colors = BufferUtils.createFloatBuffer(12);
//		FloatBuffer verticies = BufferUtils.createFloatBuffer(12);
//		if(faceNum == 0){//top, y direction
//			
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(-.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(.5f+x).put(.5f+y).put(.5f+z);
//			verticies.put(-.5f+x).put(.5f+y).put(.5f+z);
//		}else if(faceNum == 1){//south, z direction 
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(-.5f+x).put(.5f+y).put(.5f+z);
//			verticies.put(.5f+x).put(.5f+y).put(.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(.5f+z);
//			verticies.put(-.5f+x).put(-.5f+y).put(.5f+z);
//		}else if(faceNum == 2){//north, -z direction
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(-.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(-.5f+x).put(-.5f+y).put(-.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(-.5f+z);
//		}else if(faceNum == 3){//west, -x direction
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(-.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(-.5f+x).put(.5f+y).put(.5f+z);
//			verticies.put(-.5f+x).put(-.5f+y).put(.5f+z);
//			verticies.put(-.5f+x).put(-.5f+y).put(-.5f+z);
//		}else if(faceNum == 4){//east, x direction
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(.5f+x).put(.5f+y).put(.5f+z);
//			verticies.put(.5f+x).put(.5f+y).put(-.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(-.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(.5f+z);
//		}else if(faceNum == 5){//bottom, -y direction
//			textures.put(a).put(b).
//			put(a+.0625f).put(b).
//			put(a+.0625f).put(b+.0625f).
//			put(a).put(b+.0625f);
//			
//			colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
//			
//			verticies.put(-.5f+x).put(-.5f+y).put(.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(.5f+z);
//			verticies.put(.5f+x).put(-.5f+y).put(-.5f+z);
//			verticies.put(-.5f+x).put(-.5f+y).put(-.5f+z);
//		}
////		glBindBuffer(GL_ARRAY_BUFFER, Renderer.vbo.vertexHandle);
////		glBufferSubData(GL_ARRAY_BUFFER, start*Renderer.vbo.vertexSize*4, (FloatBuffer)(verticies.flip()));
////		
////		glBindBuffer(GL_ARRAY_BUFFER, Renderer.vbo.textureHandle);
////		glBufferSubData(GL_ARRAY_BUFFER, start*Renderer.vbo.textureCoordSize*4, (FloatBuffer)(textures.flip()));
////		
////		glBindBuffer(GL_ARRAY_BUFFER, Renderer.vbo.colorHandle);
////		glBufferSubData(GL_ARRAY_BUFFER, start*Renderer.vbo.colorSize*4, (FloatBuffer)(colors.flip()));
//		Runner.vbo.put(Runner.vbo.numVerticies, verticies, textures, colors);
//		
//		return start + 4;
////		}else if(faceNum == 1){//south
////			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, .5f,.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, .5f, .5f);        
////	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, .5f);
////	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, .5f); 
////		}else if(faceNum == 2){//north
////			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( .5f, .5f,-.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(-.5f, .5f,-.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(-.5f, -.5f, -.5f);
////	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( .5f, -.5f, -.5f); 
////		}else if(faceNum == 3){//west
////			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, .5f,-.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(-.5f, .5f,.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(-.5f, -.5f, .5f);
////	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, -.5f); 
////		}else if(faceNum == 4){//east
////			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( .5f, .5f, .5f);        
////	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, .5f,-.5f);        
////	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, -.5f);
////	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( .5f, -.5f, .5f); 
////		}else if(faceNum == 5){//bottom
////			GL11.glTexCoord2f(a, b); 				GL11.glVertex3f( -.5f, -.5f, .5f);        
////	        GL11.glTexCoord2f(a+.0625f, b); 		GL11.glVertex3f(.5f, -.5f, .5f);        
////	        GL11.glTexCoord2f(a+.0625f, b+.0625f); 	GL11.glVertex3f(.5f, -.5f, -.5f);
////	        GL11.glTexCoord2f(a, b+.0625f); 		GL11.glVertex3f( -.5f, -.5f, -.5f); 
////		}
//	}
	
	public static void drawWireCube(float[] blockLook){
		int x = 0;
		int y = 1;
		int z = 2;
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		//GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glColor3b((byte)0, (byte)0, (byte)0);
		float siz = .51f;
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]+siz, blockLook[z]+siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]+siz, blockLook[z]+siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]+siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]+siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]+siz, blockLook[z]+siz);
		
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]-siz, blockLook[z]+siz);
		
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]-siz, blockLook[z]+siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]-siz, blockLook[z]+siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]-siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]-siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]-siz, blockLook[z]+siz);
		
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]-siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]+siz, blockLook[y]+siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]+siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]-siz, blockLook[z]-siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]-siz, blockLook[z]+siz);
		GL11.glVertex3f(blockLook[x]-siz, blockLook[y]+siz, blockLook[z]+siz);
		GL11.glEnd();
	}
	
	
	public static int[] getAdjacentBlock(byte side, int x, int y, int z){
		switch(side){
		case Blocks.TOP:
			return new int[]{x, y+1, z};
		case Blocks.SOUTH:
			return new int[]{x, y, z+1};
		case Blocks.NORTH:
			return new int[]{x, y, z-1};
		case Blocks.WEST:
			return new int[]{x-1, y, z};
		case Blocks.EAST:
			return new int[]{x+1, y, z};
		case Blocks.BOTTOM:
			return new int[]{x, y-1, z};
		default:
			return null;
		}
	}
	
	public static int adjX(byte side, int x){
		switch(side){
		case Blocks.EAST:
			return x+1;
		case Blocks.WEST:
			return x-1;
		default:
			return x;
		}
	}
	public static int adjY(byte side, int y){
		switch(side){
		case Blocks.TOP:
			return y+1;
		case Blocks.BOTTOM:
			return y-1;
		default:
			return y;
		}
	}
	public static int adjZ(byte side, int z){
		switch(side){
		case Blocks.SOUTH:
			return z+1;
		case Blocks.NORTH:
			return z-1;
		default:
			return z;
		}
	}
}
