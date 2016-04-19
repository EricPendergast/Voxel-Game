package framework.rendering;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;

import world.Chunk;
import world.blocks.Block;

@SuppressWarnings("static-access")
public class MemoryManager {
	public VBO vbo;
	public int cur = 0;
	int unitSize;
	private LinkedList<Integer> openSpots;
	
	public MemoryManager(VBO v, int unitSize){
		vbo = v;
		this.unitSize = unitSize;
		openSpots = new LinkedList<Integer>();
		
		for(int i = 0; i < unitSize*VBO.vertexSize; i++)
			vertexEmpty.put(0);
		for(int i = 0; i < unitSize*VBO.textureCoordSize; i++)
			textureCoordEmpty.put(0);
		for(int i = 0; i < unitSize*VBO.colorSize; i++)
			colorEmpty.put(0);
	}
	
	private FloatBuffer vertexEmpty = BufferUtils.createFloatBuffer(24 * VBO.vertexSize);
	private FloatBuffer textureCoordEmpty = BufferUtils.createFloatBuffer(24 * VBO.textureCoordSize);
	private FloatBuffer colorEmpty = BufferUtils.createFloatBuffer(24 * VBO.colorSize);

	public synchronized boolean clear(int start, int length){
//		FloatBuffer[] b = {	BufferUtils.createFloatBuffer(length*vbo.vertexSize), 
//							BufferUtils.createFloatBuffer(length*vbo.textureCoordSize), 
//							BufferUtils.createFloatBuffer(length*vbo.colorSize)};
		FloatBuffer[] b;
		//if(length == 24){
		b = new FloatBuffer[]{vertexEmpty, textureCoordEmpty, colorEmpty};
		//}else{
		//	b = new FloatBuffer[] {	BufferUtils.createFloatBuffer(length*VBO.vertexSize), 
		//							BufferUtils.createFloatBuffer(length*VBO.textureCoordSize), 
		//							BufferUtils.createFloatBuffer(length*VBO.colorSize)};
		//}
//		b[0].clear();
//		b[1].clear();
//		b[2].clear();
//		for(int i = 0; i < unitSize*VBO.vertexSize; i++)
//			b[0].put(0);
//		for(int i = 0; i < unitSize*VBO.textureCoordSize; i++)
//			b[1].put(0);
//		for(int i = 0; i < unitSize*VBO.colorSize; i++)
//			b[2].put(0);
//		b[0].put(new float[length*vbo.vertexSize]);
//		b[1].put(new float[length*vbo.textureCoordSize]);
//		b[2].put(new float[length*vbo.colorSize]);
		return vbo.put(start, new VertexInfo(b[0], b[1], b[2]));
	}
//	public void addFace(int start, byte faceNum, Block block, int x, int y, int z){
//		FloatBuffer[] b = block.getRenderInfo(faceNum, x, y, z);
//		if(b == null)
//			return;
//		put(start, b[0], b[1], b[2]);
//	}
	public synchronized boolean put(int start, VertexInfo info){
		if(vbo.put(start, info)){
			Chunk.debugCounter++;
			cur += info.pos.capacity()/VBO.vertexSize;
			return true;
		}
		return false;
		
	}
	/**
	 * Puts the sides of 'type' into the vbo and returns the index(es) of where the rendering info was put
	 * @param sidesOccupied
	 * @param type
	 * @return
	 */
	
	public synchronized int renderBlock(Block[] sidesOccupied, Block type, int x, int y, int z){
		
		if(openSpots.size() == 0){
			int start = cur;
			VertexInfo vinfo = type.getRenderInfoNew(sidesOccupied, x, y, z);
			if(vinfo == null)
				return -1;
			//if(b[0] == null)
			//	return -1;
			//this if statement tells whatever called this method that it tried to render the block, but couldn't because the vbo FloatBuffer pool ran out
			if(put(start, vinfo) == false)
				return -2;
			
			return start;
		}else{
			int start = openSpots.peekLast();
			
			VertexInfo vinfo = type.getRenderInfoNew(sidesOccupied, x, y, z);
			if(vinfo == null)
				return -1;
			//if(b[0] == null)
			//	return -1;
			if(put(start, vinfo) == false)
				return -2;
			
			openSpots.removeLast();
			return start;
		}
		
	}
	
	public synchronized boolean removeBlock(int index){
		if(clear(index, 4*6)){
			openSpots.addFirst(index);
			return true;
		}else
			return false;
	}
	
	public int approxSize(){
		return cur;
	}
}
