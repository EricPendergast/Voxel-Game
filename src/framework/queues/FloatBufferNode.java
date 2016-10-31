package framework.queues;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import framework.rendering.VBO;

public class FloatBufferNode extends ConcurrentNode{
	public FloatBuffer vert;
	public FloatBuffer col;
	public FloatBuffer tex;
	public FloatBufferNode(int size){
		vert = BufferUtils.createFloatBuffer(size*VBO.vertexSize);
		col = BufferUtils.createFloatBuffer(size*VBO.colorSize);
		tex = BufferUtils.createFloatBuffer(size*VBO.textureCoordSize);
	}
	
	

}
