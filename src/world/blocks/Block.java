package world.blocks;

import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import framework.Blocks;
import framework.Ops;
import framework.OpsMut;
import framework.rendering.VertexInfo;
import world.Entity;
import world.PhysicsBody;
import world.Planet;
import world.Player;

public abstract class Block {
	public static VertexInfo vinfo = new VertexInfo(24);
	public static FloatBuffer textures = null;
	public static FloatBuffer colors = null;
	public static FloatBuffer verticies = null;
	static{
		textures = BufferUtils.createFloatBuffer(8*6);
		colors = BufferUtils.createFloatBuffer(12*6);
		verticies = BufferUtils.createFloatBuffer(12*6);
	}
	public byte id;
	public static long lastTime = System.currentTimeMillis();
	public boolean collide(int x, int y, int z, Planet planet, PhysicsBody body, int hitboxIndex){
		float[] move = Ops.collide(body.getHitbox(hitboxIndex), new float[]{x,y,z,.5f});
		
		if(move != null){
			
			int nonzeroIndex = -1;
			for(int i = 0; i < 3; i++)
				if(move[i] != 0)
					nonzeroIndex = i;
			
			
			if(nonzeroIndex != -1 && Math.abs(move[nonzeroIndex]) < (.5f - body.getHitbox(hitboxIndex)[3]) * 2){
				//System.out.println("collided" + Ops.arrToString(move));
				//System.out.println(Ops.arrToString(body.dir));
				//System.out.println(Math.abs(move[nonzeroIndex]));
				OpsMut.add(body.pos, move);
				
				//if(body.dir[nonzeroIndex] > 0 != move[nonzeroIndex] > 0){//System.out.println("asdf");
					body.dir[nonzeroIndex] = 0;
				//}
				//if(body.movement[nonzeroIndex] > 0 != move[nonzeroIndex] > 0){
					body.movement[nonzeroIndex] = 0;
				//}
				
				float[] temp = Ops.multiply(body.gravity, -1);
				float[] temp2 = Ops.copy(temp);
				temp2[nonzeroIndex] = 0;
				float angle = Ops.getAngleBetween(temp, temp2);
				//System.out.println(angle);
				if(Math.abs(angle) > body.lowestCollideAng){
					body.hitboxCollided[hitboxIndex] = true;
				}
				return true;
			}
		}
		return false;
//		if(true)
//			return;
//		float[] sp = body.getHitbox(hitboxIndex);
//		if(!isSolid())
//			return;
//		float[] dir = Ops.add(body.movement, body.dir);
//		boolean collide = false;
//		float ang = .0f;
//		float[] temp = Ops.multiply(body.gravity, -1);
//		//these are used to find the angle between the player and any of the 6 possible faces the player collided with
//		float angx = Ops.getAngleBetween(temp, new float[]{0,temp[1],temp[2]});
//		float angy = Ops.getAngleBetween(temp, new float[]{temp[0],0,temp[2]});
//		float angz = Ops.getAngleBetween(temp, new float[]{temp[0],temp[1],0});
//		temp = null;
//		
//		boolean isSurrounded = 	planet.getBlock(x,y+1,z).isSolid() && planet.getBlock(x,y-1,z).isSolid() && 
//								planet.getBlock(x+1,y,z).isSolid() && planet.getBlock(x-1,y,z).isSolid() &&
//								planet.getBlock(x,y,z+1).isSolid() && planet.getBlock(x,y,z-1).isSolid();
//		if(sp[Ops.y] <= y+.5f+sp[Ops.radius] && Math.abs(sp[Ops.y]-y)-ang > Math.max(Math.abs(sp[Ops.x]-x),Math.abs(sp[Ops.z]-z)) && sp[Ops.y] > y && dir[Ops.y] <= 0 &&
//			(new Rectangle2D.Float(sp[Ops.x]-sp[Ops.radius], sp[Ops.z]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(x-.5f,z-.5f,1,1)) &&
//			(!planet.getBlock(x,y+1,z).isSolid() || isSurrounded)){
//			body.pos[Ops.y] += y+.5f+sp[Ops.radius] - sp[Ops.y];
//			if(body.dir[Ops.y] < 0)
//				body.dir[Ops.y] = 0;
//			if(body.movement[Ops.y] < 0)
//				body.movement[Ops.y] = 0;
//			if(Math.abs(angy) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		else if(sp[Ops.x] <= x+.5f+sp[Ops.radius] && Math.abs(sp[Ops.x]-x)-ang > Math.max(Math.abs(sp[Ops.y]-y),Math.abs(sp[Ops.z]-z)) && sp[Ops.x] > x && dir[Ops.x] <= 0 &&
//			(new Rectangle2D.Float(sp[Ops.y]-sp[Ops.radius], sp[Ops.z]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(y-.5f,z-.5f,1,1)) &&
//			(!planet.getBlock(x+1,y,z).isSolid() || isSurrounded)){
//			body.pos[Ops.x] += x+.5f+sp[Ops.radius] - sp[Ops.x];
//			if(body.dir[Ops.x] < 0)
//				body.dir[Ops.x] = 0;			
//			if(body.movement[Ops.x] < 0)
//				body.movement[Ops.x] = 0;
//			if(Math.abs(angx) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		else if(sp[Ops.z] <= z+.5f+sp[Ops.radius] && Math.abs(sp[Ops.z]-z)-ang > Math.max(Math.abs(sp[Ops.x]-x),Math.abs(sp[Ops.y]-y)) && sp[Ops.z] > z && dir[Ops.z] <= 0 &&
//			(new Rectangle2D.Float(sp[Ops.x]-sp[Ops.radius], sp[Ops.y]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(x-.5f,y-.5f,1,1)) &&
//			(!planet.getBlock(x,y,z+1).isSolid() || isSurrounded)){
//			body.pos[Ops.z] += z+.5f+sp[Ops.radius] - sp[Ops.z];
//			if(body.dir[Ops.z] < 0)
//				body.dir[Ops.z] = 0;
//			if(body.movement[Ops.z] < 0)
//				body.movement[Ops.z] = 0;
//			
//			if(Math.abs(angz) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		else if(sp[Ops.y] >= y-.5f-sp[Ops.radius] && Math.abs(sp[Ops.y]-y)-ang > Math.max(Math.abs(sp[Ops.x]-x),Math.abs(sp[Ops.z]-z)) && sp[Ops.y] < y && dir[Ops.y] >= 0 &&
//			(new Rectangle2D.Float(sp[Ops.x]-sp[Ops.radius], sp[Ops.z]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(x-.5f,z-.5f,1,1)) &&
//			(!planet.getBlock(x,y-1,z).isSolid() || isSurrounded)){
//			body.pos[Ops.y] += y-.5f-sp[Ops.radius] - sp[Ops.y];
//			if(body.dir[Ops.y] > 0)
//				body.dir[Ops.y] = 0;
//			if(body.movement[Ops.y] > 0)
//				body.movement[Ops.y] = 0;
//			
//			if(Math.abs(angy) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		else if(sp[Ops.x] >= x-.5f-sp[Ops.radius] && Math.abs(sp[Ops.x]-x)-ang > Math.max(Math.abs(sp[Ops.y]-y),Math.abs(sp[Ops.z]-z)) && sp[Ops.x] < x && dir[Ops.x] >= 0 &&
//			(new Rectangle2D.Float(sp[Ops.y]-sp[Ops.radius], sp[Ops.z]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(y-.5f,z-.5f,1,1)) &&
//			(!planet.getBlock(x-1,y,z).isSolid() || isSurrounded)){
//			body.pos[Ops.x] += x-.5f-sp[Ops.radius] - sp[Ops.x];
//			if(body.dir[Ops.x] > 0)
//				body.dir[Ops.x] = 0;
//			if(body.movement[Ops.x] > 0)
//				body.movement[Ops.x] = 0;
//			
//			if(Math.abs(angx) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		else if(sp[Ops.z] >= z-.5f-sp[Ops.radius] && Math.abs(sp[Ops.z]-z)-ang > Math.max(Math.abs(sp[Ops.x]-x),Math.abs(sp[Ops.y]-y)) && sp[Ops.z] < z && dir[Ops.z] >= 0 &&
//			(new Rectangle2D.Float(sp[Ops.x]-sp[Ops.radius], sp[Ops.y]-sp[Ops.radius], sp[Ops.radius]*2,sp[Ops.radius]*2)).intersects(
//			new Rectangle2D.Float(x-.5f,y-.5f,1,1)) &&
//			(!planet.getBlock(x,y,z-1).isSolid() || isSurrounded)){
//			body.pos[Ops.z] += z-.5f-sp[Ops.radius] - sp[Ops.z];
//			if(body.dir[Ops.z] > 0)
//				body.dir[Ops.z] = 0;
//			if(body.movement[Ops.z] > 0)
//				body.movement[Ops.z] = 0;
//			
//			if(Math.abs(angz) > body.lowestCollideAng){
//				collide = true;
//			}
//		}
//		if(collide)
//			body.hitboxCollided[hitboxIndex] = true;
			
	}
//	public void render(int x, int y, int z, int start){
//		for(byte i = 0; i < 6; i++)
//			renderFace(i,x,y,z,start);
//	}
	
	static float[] color1;
	static float[] color2;
	static float[] color3;

	static{
		float br = 1;
		color1 = new float[]{br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f};
		br = .85f;
		color2 = new float[]{br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f};
		br = .70f;
		color3 = new float[]{br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f,	br,br,br-.00f};
	}
	public VertexInfo getRenderInfoNew(Block[] surround, int x, int y, int z){
		//FloatBuffer[] renderInfo = new FloatBuffer[3];
		int numSurround = 0;
//		FloatBuffer textures = null;
//		FloatBuffer colors = null;
//		FloatBuffer verticies = null;
//		textures = BufferUtils.createFloatBuffer(8*6);
//		colors = BufferUtils.createFloatBuffer(12*6);
//		verticies = BufferUtils.createFloatBuffer(12*6);
		////
		vinfo.tex.clear();
		vinfo.col.clear();
		vinfo.pos.clear();
		for(int i = 0; i < vinfo.tex.capacity(); i++)
			vinfo.tex.put(0);
		for(int i = 0; i < vinfo.col.capacity(); i++)
			vinfo.col.put(0);
		for(int i = 0; i < vinfo.pos.capacity(); i++)
			vinfo.pos.put(0);
		vinfo.tex.clear();
		vinfo.col.clear();
		vinfo.pos.clear();
		////
		
		for(int i = 0; i < 6; i++){
			
			float a = (float)(.0625 * (id%16));
			float b = (float)(.0625 * (id/16));
			
			
			if(!surround[i].isSolid()){///*surround[i].id == Blocks.UNGENERATED || */ surround[i].id == Blocks.AIR){
				float br;
				switch(i){
				case 0://top, y direction
					vinfo.pos.put(-.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(.5f+x).put(.5f+y).put(.5f+z);
					vinfo.pos.put(-.5f+x).put(.5f+y).put(.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					//colors.put(new float[]{1,1,1, 1,1,1, 1,1,1, 1,1,1});
					br = 1f;
					vinfo.col.put(color1);

					break;
				case 1://south, z direction 
					vinfo.pos.put(-.5f+x).put(.5f+y).put(.5f+z);
					vinfo.pos.put(.5f+x).put(.5f+y).put(.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(.5f+z);
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					br = .85f;
					vinfo.col.put(color2);
					
					break;
				case 2://north, -z direction
					vinfo.pos.put(.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(-.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(-.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(-.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					br = .85f;
					vinfo.col.put(color2);
					
					break;
				case 3://west, -x direction
					vinfo.pos.put(-.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(-.5f+x).put(.5f+y).put(.5f+z);
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(.5f+z);
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(-.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					br = .70f;
					vinfo.col.put(color3);
					
					break;
				case 4://east, x direction
					vinfo.pos.put(.5f+x).put(.5f+y).put(.5f+z);
					vinfo.pos.put(.5f+x).put(.5f+y).put(-.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(-.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					br = .70f;
					vinfo.col.put(color3);
					
					break;
				case 5://bottom, -y direction
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(.5f+z);
					vinfo.pos.put(.5f+x).put(-.5f+y).put(-.5f+z);
					vinfo.pos.put(-.5f+x).put(-.5f+y).put(-.5f+z);
					
					vinfo.tex.put(a).put(b).
					put(a+.0625f).put(b).
					put(a+.0625f).put(b+.0625f).
					put(a).put(b+.0625f);
					
					br = 1f;
					vinfo.col.put(color1);
					
					break;
				}
				numSurround++;
			}
			
			
//			if(numSurround == 1){//if the first side has been rendered, initialize the first index of renderInfo to hold the buffers
//				renderInfo[0] = verticies;
//				renderInfo[1] = textures;
//				renderInfo[2] = colors;
//			}
		}
		if(numSurround >= 1)
			return vinfo;
		else
			return null;
	}
//	public int renderFace(byte direction, int x, int y, int z, int start){
//		return Blocks.addFace(direction, id, x, y, z, start);
//	}
	
	public boolean isSolid(){
		return true;
	}
}
