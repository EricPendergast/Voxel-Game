package world.blocks;

import java.nio.FloatBuffer;

import framework.rendering.VertexInfo;
import world.Entity;
import world.PhysicsBody;
import world.Planet;
import world.Player;

public class Air extends Block{
	public Air(){
		id = 0;
	}
	public boolean collide(int x, int y, int z, Planet planet, PhysicsBody body, int hitboxIndex){return false;}
	public boolean isSolid(){
		return false;
	}
	
	public VertexInfo getRenderInfoNew(Block[] surround, int x, int y, int z){
		return null;
	}
}
