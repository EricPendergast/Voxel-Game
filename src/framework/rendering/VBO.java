package framework.rendering;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import framework.Timer;
import framework.queues.ConcurrentQueue;
import framework.queues.FloatBufferNode;
import framework.queues.VertexNode;

public class VBO {
	public final ConcurrentQueue<VertexNode> toRender = new ConcurrentQueue<VertexNode>();
	public final ConcurrentQueue<VertexNode> bufferPool = new ConcurrentQueue<VertexNode>();
	public static final int floatSize = 4;
	public static final int vertexSize = 3;
	public static final int colorSize = 3;
	public static final int textureCoordSize = 2;
	public static final int pointSize = 3;
	public final int maxPolys = 10000000;
	public int vertexHandle;
	public int colorHandle;
	public int textureHandle;
	public int numVerticies = 0;
	
	public VBO(){
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(maxPolys*vertexSize);
		vertexData.put(new float[maxPolys*vertexSize]);
		vertexData.flip();
		
		FloatBuffer colorData = BufferUtils.createFloatBuffer(maxPolys*colorSize);
		colorData.put(new float[maxPolys*colorSize]);
		colorData.flip();
		
		FloatBuffer textureData = BufferUtils.createFloatBuffer(maxPolys*textureCoordSize);
		textureData.put(new float[maxPolys*textureCoordSize]);
		textureData.flip();
		   
		vertexHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		colorHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		   
		textureHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
		glBufferData(GL_ARRAY_BUFFER, textureData, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		
		for(int i = 0; i < 10000; i++){
			bufferPool.push(new VertexNode(24));
		}
	}
	public void render(){
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
	    glVertexPointer(pointSize, GL_FLOAT, 0, 0L);
		
	    glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
	    glColorPointer(colorSize, GL_FLOAT, 0, 0L);
	       
	    glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
	    glTexCoordPointer(textureCoordSize, GL_FLOAT, 0, 0L);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glEnableClientState(GL_COLOR_ARRAY);
	    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	    
	    glDrawArrays(GL11.GL_QUADS, 0, numVerticies);
	    glDisableClientState(GL_COLOR_ARRAY);
	    glDisableClientState(GL_VERTEX_ARRAY);
	    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}
	public synchronized boolean put(int start, VertexInfo i){
		VertexNode buffers = bufferPool.pop();
		if(buffers == null)
			return false;
		buffers.info.start = start;
		buffers.info.setBuffers(i);
		toRender.push(buffers);
		return true;
	}
	
	public void sendVerticies(int timeLeft){
		if(Keyboard.isKeyDown(Keyboard.KEY_Z))
			return;
		//if(toRender.size() > 0)
		//	System.out.println(toRender.size());
		//int count = 0;
		Timer t = new Timer();
		t.start();
		int size = toRender.size();
		while(t.getTime() < timeLeft && size > 0){
			size--;
			//if(count > 1)
			//	return;
			VertexNode n = toRender.pop();
			if(n != null){
				send(n.info.start, n.info);
				bufferPool.push(n);
			}
			//count++;
		}
	}
	public void delete(){
		glDeleteBuffers(vertexHandle);
		glDeleteBuffers(colorHandle);
		glDeleteBuffers(textureHandle);
	}
	
	private void send(int start, VertexInfo vinfo){
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glBufferSubData(GL_ARRAY_BUFFER, start*vertexSize*floatSize, (FloatBuffer)(vinfo.pos.flip()));
		
		glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
		glBufferSubData(GL_ARRAY_BUFFER, start*textureCoordSize*floatSize, (FloatBuffer)(vinfo.tex.flip()));
		
		glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
		glBufferSubData(GL_ARRAY_BUFFER, start*colorSize*floatSize, (FloatBuffer)(vinfo.col.flip()));
		
		numVerticies = Math.max(numVerticies, start + vinfo.pos.capacity()/vertexSize);
	}
}

//class VertexQueue{
//	
//	public VertexQueue(){
//		
//	}
//	VertexNode first;
//	VertexNode last;
//	
//	public void push(int start, FloatBuffer verticies, FloatBuffer textures, FloatBuffer colors){
//		VertexNode v = new VertexNode(start, verticies, textures, colors);
//		if(first != null){
//			v.prev = first;
//			first.next = v;
//		}
//		first = v;
//	}
//	public VertexNode pop(){
//		VertexNode ret = last;
//		if(last != null){
//			last = last.next;
//			if(last != null)
//				last.prev = null;
//		}
//		
//		return ret;
//	}
//}
//
