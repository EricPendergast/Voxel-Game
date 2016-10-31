package framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class GUI {
	Texture tex;
	DisplayMode displayMode;
	public GUI(String textureLoc, DisplayMode displayMode){
		tex = loadTexture(textureLoc); 
		this.displayMode = displayMode;
	}
	
	public void render(){
		if(true)
			return;
//		tex.bind();
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glColor3f(255, 255, 255);
//		GL11.glTexCoord2f(0, 0);GL11.glVertex2f( 0f, 0f);
//		GL11.glTexCoord2f(1, 0);GL11.glVertex2f( 100f, 0f);
//		GL11.glTexCoord2f(1, 1);GL11.glVertex2f( 100f, 100f);
//		GL11.glTexCoord2f(0, 1);GL11.glVertex2f( 0f, 100f);
//		
//		GL11.glEnd();
		
		int size = 100;
		ByteBuffer buffer = BufferUtils.createByteBuffer(size*size*3);
		
		GL11.glReadPixels(displayMode.getWidth()/2, displayMode.getHeight()/2, size, size, GL11.GL_RGB, GL11.GL_BYTE, buffer);
		
		for(int i = 0; i < size*size*3; i++){
			int cur = buffer.get(i);
			//System.out.println("A" + buffer.get(i));
			cur = (byte)(255 - (cur+128));
			buffer.put(i, (byte)cur);
			//System.out.println("B" + buffer.get(i));
		}
		//System.out.println((byte))
		GL14.glWindowPos2f(displayMode.getWidth()/2, displayMode.getHeight()/2);
		GL11.glDrawPixels(size, size, GL11.GL_RGB, GL11.GL_BYTE, buffer);
	}
	private Texture loadTexture(String file){
		try {
			return TextureLoader.getTexture("png", new FileInputStream(new File(file)), GL11.GL_NEAREST);
		}catch (IOException e) { 
			System.err.println("Failed to load texture file");
		}
		
		return null;
	}
}
