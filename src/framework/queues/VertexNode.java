package framework.queues;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import framework.rendering.VBO;
import framework.rendering.VertexInfo;

public class VertexNode extends ConcurrentNode{
//	public FloatBuffer vert;
//	public FloatBuffer col;
//	public FloatBuffer tex;
//	public int start;
	public VertexInfo info;
	public VertexNode(int size){
//		start = 0;
//		vert = BufferUtils.createFloatBuffer(size*VBO.vertexSize);
//		col = BufferUtils.createFloatBuffer(size*VBO.colorSize);
//		tex = BufferUtils.createFloatBuffer(size*VBO.textureCoordSize);
		info = new VertexInfo(size);
	}
}