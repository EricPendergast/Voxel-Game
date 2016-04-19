package world.blocks;

import java.nio.FloatBuffer;

import framework.rendering.VertexInfo;

public class Ungenerated extends Block {
	public Ungenerated(){
		id = 2;
	}
	public boolean isSolid(){
		return false;
	}
	public VertexInfo getRenderInfoNew(Block[] surround, int x, int y, int z){
		return null;
	}
}
