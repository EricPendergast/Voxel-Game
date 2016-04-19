package framework.rendering;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class VertexInfo {
	public FloatBuffer pos;
	public FloatBuffer tex;
	public FloatBuffer col;
	public int start;
	public VertexInfo(int size){
		pos = BufferUtils.createFloatBuffer(size*VBO.vertexSize);
		tex = BufferUtils.createFloatBuffer(size*VBO.textureCoordSize);
		col = BufferUtils.createFloatBuffer(size*VBO.colorSize);
	}
	public VertexInfo(){}
	
	public VertexInfo(FloatBuffer p, FloatBuffer t, FloatBuffer c){
		pos = p;
		tex = t;
		col = c;
	}
	public void setBuffers(FloatBuffer p, FloatBuffer t, FloatBuffer c){
		pos.clear();
		for(int i = 0; i < pos.capacity(); i++){
			pos.put(p.get(i));
		}
		col.clear();
		for(int i = 0; i < col.capacity(); i++){
			col.put(c.get(i));
		}
		tex.clear();
		for(int i = 0; i < tex.capacity(); i++){
			tex.put(t.get(i));
		}
	}
	public void setBuffers(VertexInfo i){
		setBuffers(i.pos, i.tex, i.col);
	}
}
