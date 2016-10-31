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
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import framework.Timer;
import framework.queues.ConcurrentQueue;
import framework.queues.FloatBufferNode;
import framework.queues.VertexNode;

//A wrapper class for opengl's vertex buffer objects.
public class VBO {
	// 'bufferPools' contains separate float buffer pools that are intended to
	// be used for different things. The reason more than one buffer pool is
	// needed is that no one source can use up all the buffers before the other
	// sources can use what they need.
	//Example: world generation uses many float buffers, but whenever the player
	//updates a block, they only need 1 float buffer. If there is only one
	// buffer pool, and the world generator uses up all the float buffers in the
	// pool, the player only needs one buffer but can't get it.
	public final ArrayList<ConcurrentQueue<VertexNode>> bufferPools = new ArrayList<ConcurrentQueue<VertexNode>>();
//	// Each of these ints refer to an index in 'bufferPools'. They are all
//	// called 'from<something>' because classes outside the VBO class should not
//	// know about how the VBO class handles buffers, all they are doing is
//	// telling the VBO who they are, and the VBO uses that information to store
//	// their VertexInfo's in the correct buffer pool
//	//'fromPlayer' refers to the buffer pool in 'bufferPools' which should
//	// only be used by the player.
//	public static final int fromPlayer = 0;
//	// the world gen buffer pool should only be used for world generation. ie
//	// when new blocks are being added by the world loader/generator
//	public static final int fromWorldGen = 1;
//	// The world clear buffer should only be used for deleting blocks
//	public static final int fromWorldClear = 2;
//
	// The array of lists of float buffers to be rendered. bufferPools.get(i)
	// should always be pushed to toRender.get(i)
	public final ArrayList<ConcurrentQueue<VertexNode>> toRender = new ArrayList<ConcurrentQueue<VertexNode>>();
	// VertexInfos are dequeued from this queue and enqueued to 'toRender'
	// whenever a new VertexInfo needs to be rendered.
//	public final ConcurrentQueue<VertexNode> bufferPool = new ConcurrentQueue<VertexNode>();
	
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
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(maxPolys * vertexSize);
		vertexData.put(new float[maxPolys * vertexSize]);
		vertexData.flip();
		
		FloatBuffer colorData = BufferUtils.createFloatBuffer(maxPolys * colorSize);
		colorData.put(new float[maxPolys * colorSize]);
		colorData.flip();
		
		FloatBuffer textureData = BufferUtils.createFloatBuffer(maxPolys * textureCoordSize);
		textureData.put(new float[maxPolys * textureCoordSize]);
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
		
		// Initializes the buffer pool with a certain number of VertexNodes
//		for(int i = 0; i < 10000; i++){
//			bufferPool.push(new VertexNode(24));
//		}
		// Initializing the arraylist of buffer pools
		for(int i = 0; i < 3; i++){
			bufferPools.add(new ConcurrentQueue<VertexNode>());
			toRender.add(new ConcurrentQueue<VertexNode>());
		}
		for(int i = 0; i < 10000; i++)
			bufferPools.get(VBOSource.fromWorldGen.getBufferIndex()).push(new VertexNode(24));
		
		for(int i = 0; i < 10000; i++)
			bufferPools.get(VBOSource.fromWorldClear.getBufferIndex()).push(new VertexNode(24));
		
		for(int i = 0; i < 10000; i++)
			bufferPools.get(VBOSource.fromPlayer.getBufferIndex()).push(new VertexNode(24));
		
	}
	// Renders the vbo stored vertexes to the screen
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
	//Copies 'i' into a new VertexInfo object dequeued from
	// the buffer pool specified 'bufferPoolType'. This new VertexInfo is then pushed to the
	//render queue specified by 'bufferPoolType'
	public synchronized boolean put(int start, VertexInfo i, VBOSource source){
		VertexNode buffers = bufferPools.get(source.getBufferIndex()).pop();
		if(buffers == null)
			return false;
		buffers.info.start = start;
		buffers.info.setBuffers(i);
		toRender.get(source.getBufferIndex()).push(buffers);
		return true;
	}
	// Spends 'timeLeft' milliseconds sending the verticies in 'toRender' to
	// opengl's graphics memory.
	public void sendVerticies(int timeLeft){
		if(Keyboard.isKeyDown(Keyboard.KEY_Z))
			return;
		//if(toRender.size() > 0)
		//	System.out.println(toRender.size());
		//int count = 0;
		Timer t = new Timer();
		t.start();
		
		while(t.getTime() < timeLeft){
			// This loop sends one vertex from each of the buffer pools in 'toRender'
			for(int i = 0; i < toRender.size(); i++){
				VertexNode n = toRender.get(i).pop();
				if(n != null){
					if(i == 0 && toRender.get(0).size() > 0){
						System.out.println("YES" + toRender.get(0).size());
					}
					// Renders the vertex info
					send(n.info.start, n.info);
					bufferPools.get(i).push(n);
					
				}
			}
			
		}
	}
	// Deletes all the data stored in opengl's grapical memory
	public void delete(){
		glDeleteBuffers(vertexHandle);
		glDeleteBuffers(colorHandle);
		glDeleteBuffers(textureHandle);
	}
	// Sends the given VertexInfo to opengl's graphical memory
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
