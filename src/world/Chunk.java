package world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import world.blocks.Air;
import world.blocks.Block;
import framework.Blocks;
import framework.Ops;
import framework.Runner;
import framework.Timer;
import framework.rendering.Renderer;

//NOTE: when passing coords into methods and returning coords, it is always absolute
public class Chunk {
	public static String debugString;
	public static final int CHUNK_SIZE = 16;
	byte[][][] blocks;
	int[][][] renderLocs;
	//location of this chunk
	public int xc,yc,zc;
	//-1 is unspecified
	//0 is close up
	//1 is far away and updating
	//2 is far away with no updating
	private int mode = -1;
	//The start of the vertices it renders
	private int chunkStart;
	private Planet planet;
	
	private boolean isRendered = false;
	private boolean isGenerated = false;
	public Chunk(int x, int y, int z, Planet planet){
		xc = x;
		yc = y;
		zc = z;
		this.planet = planet;
		blocks = new byte[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		renderLocs = new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 16; k++){
					renderLocs[i][j][k] = -1;
					blocks[i][j][k] = Blocks.UNGENERATED;
				}
			}
		}
	}
	
	public void update(){
		
	}
	public void clearGraphics(){
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 16; k++){
					if(renderLocs[i][j][k] != -1){
						Renderer.mem.removeBlock(renderLocs[i][j][k]);
						renderLocs[i][j][k] = -1;
					}
				}
			}
		}
	}
//	private static int ci = 0;
//	private static int cj = 0;
//	private static int ck = 0;
//	public boolean clearGraphicsPart(int time){
//		Timer timer = new Timer();
//		timer.start();
//		
//		for(;ci < 16; ci++){
//			for(; cj < 16; cj++){
//				for(; ck < 16; ck++){
//					if(renderLocs[ci][cj][ck] != -1){
//						Renderer.mem.removeBlock(renderLocs[ci][cj][ck]);
//						renderLocs[ci][cj][ck] = -1;
//						if(timer.getTime() >= time){
//							return false;
//						}
//					}
//				}
//				ck = 0;
//			}
//			cj = 0;
//		}
//		ci = 0;
//		return true;
//	}
	
	private int ci = 0;
	private int cj = 0;
	private int ck = 0;
	public boolean clearGraphicsPart(){
		isRendered = false;
		for(;ci < 16; ci++){
			for(; cj < 16; cj++){
				for(; ck < 16; ck++){
					if(renderLocs[ci][cj][ck] != -1){
						if(Renderer.mem.removeBlock(renderLocs[ci][cj][ck]) == false)
							return false;
						renderLocs[ci][cj][ck] = -1;
					}
				}
				ck = 0;
			}
			cj = 0;
		}
		ci = 0;
		return true;
	}
	//renders as much of the chunk it can in the given amount of time
	private int i,j,k;
	public boolean render(){
		int[] pos = new int[4];
		for(; i < 16; i++){
			for(; j < 16; j++){
				for(; k < 16; k++){
					pos[0] = i; pos[1] = j; pos[2] = k;
					pos[identity] = Chunk.local;
					toAbsoluteMut(pos);
					if(updateBlockGraphicsNoGarbage(pos) == false)
						return false;
				}
				k = 0;
			}
			j = 0;
		}
		i = 0;
		isRendered = true;
		return true;
	}
//	private int i,j,k;
//	public void renderPart(int time){
//		Timer timer = new Timer();
//		timer.start();
//		int[] pos = new int[4];
//		for(; i < 16; i++){
//			for(; j < 16; j++){
//				for(; k < 16; k++){
//					if(timer.getTime() >= time){
//						return;
//					}
//					pos[0] = i; pos[1] = j; pos[2] = k;
//					pos[identity] = Chunk.local;
//					toAbsoluteMut(pos);
//					updateBlockGraphicsNoGarbage(pos);
//				}
//				k = 0;
//			}
//			j = 0;
//		}
//		i = 0;
//		isRendered = true;
//	}
	//generates as much of the chunk it can in the given amount of time
//	private int gi,gj,gk;
//	public void generatePart(int time){
//		Timer timer = new Timer();
//		timer.start();
//		for(; gi < 16; gi++){
//			for(; gj < 16; gj++){
//				for(; gk < 16; gk++){
//					if(timer.getTime() >= time){
//						return;
//					}
//					blocks[gi][gj][gk] = planet.generate(xc*CHUNK_SIZE+gi, yc*CHUNK_SIZE+gj, zc*CHUNK_SIZE+gk);
//				}
//				gk = 0;
//			}
//			gj = 0;
//		}
//		gi = 0;
//		isGenerated = true;
//	}
	public void generate(){
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 16; j++){
				for(int k = 0; k < 16; k++){
					blocks[i][j][k] = planet.generate(xc*CHUNK_SIZE+i, yc*CHUNK_SIZE+j, zc*CHUNK_SIZE+k);
				}
			}
		}
		isGenerated = true;
	}
	static Timer t2 = new Timer();
	static Block[] surround = new Block[6];
	static int[] temp = new int[4];
	static{
		
	}
	public static int debugCounter = 0;
	public boolean updateBlockGraphicsNoGarbage(int[] blockPos){//bug found: slowed down by garbage collection
		toLocalMut(blockPos);
		
		//t2.start();
		
		int i = blockPos[0];
		int j = blockPos[1];
		int k = blockPos[2];
//		if(Keyboard.isKeyDown(Keyboard.KEY_H) && renderLocs[i][j][k] != -1)
//			System.out.println("Chunk " + renderLocs[i][j][k]);
		if(renderLocs[i][j][k] != -1){
			Renderer.mem.removeBlock(renderLocs[i][j][k]);
			renderLocs[i][j][k] = -1;
		}
		
		toAbsoluteMut(blockPos);

		if(!Blocks.list[blocks[i][j][k]].isSolid())
			return true;
		
		//fill 'surround' with the 6 blocks around the block that will be rendered
		//the value of 'n' corresponds to  Blocks.TOP,Blocks.BOTTOM, etc.
		for(byte n = 0; n < 6; n++){
			temp[0] = Blocks.adjX(n, blockPos[Ops.x]); temp[1] = Blocks.adjY(n, blockPos[Ops.y]); temp[2] = Blocks.adjZ(n, blockPos[Ops.z]);
			temp[Chunk.identity] = Chunk.absolute;
			Block block = planet.getBlock(temp);//planet.getBlock(Blocks.adjX(n, blockPos[Ops.x]), Blocks.adjY(n, blockPos[Ops.y]), Blocks.adjZ(n, blockPos[Ops.z]));
			surround[n] = block;
		}
		
		renderLocs[i][j][k] = Renderer.mem.renderBlock(surround, Blocks.list[blocks[i][j][k]], blockPos[Ops.x] , blockPos[Ops.y], blockPos[Ops.z]);
		//if the buffer pool runs out
		if(renderLocs[i][j][k] == -2){
			renderLocs[i][j][k] = -1;
			return false;
		}else{
			return true;
		}
	}
	
	public void setMode(int m){
		
	}
	
	public byte getBlock(int[] pos){
		int[] rel = toLocal(pos);
		return blocks[rel[0]][rel[1]][rel[2]];
	}
	public void setBlock(int[] pos, byte id){
		toLocalMut(pos);
		blocks[pos[0]][pos[1]][pos[2]] = id;
		toAbsoluteMut(pos);
		updateBlockGraphicsNoGarbage(pos); 
		for(byte n = 0; n < 6; n++){
			int[] adj = new int[]{Blocks.adjX(n, pos[0]),Blocks.adjY(n, pos[1]),Blocks.adjZ(n, pos[2]),pos[identity]};
			
			Chunk c = planet.getChunk(adj);
			
			if(c != null)
				c.updateBlockGraphicsNoGarbage(adj);
		}
	}
	
	public void setBlockNoRenderUpdate(int[] pos, byte id){
		toLocalMut(pos);
		blocks[pos[0]][pos[1]][pos[2]] = id;
	}
	
	public String toString(){
		String ret = "";
		
		ret += "Chunk Position: (" + xc + ", " + yc + ", " + zc + ")";
		
		return ret; 
	}
	
	public boolean isRendered(){return isRendered;}
	public boolean isGenerated(){return isGenerated;}
	
	/**
	 * Identifies the coordinate as being relative to the planet
	 */
	public static final int absolute = 0;
	/**
	 * Identifies the coordinate as being a chunk position relative to the planet
	 */
	public static final int chunk = 1;
	/**
	 * Identifies the coordinate as being relative to the chunk
	 */
	public static final int local = 2;
	/**
	 * Index of the identifier for a coordinate
	 */
	public static final int identity = 3;
	
	public int[] toAbsolute(int[] p){
		if(p[identity] == Chunk.local)
			return new int[]{p[Ops.x] + xc*CHUNK_SIZE, p[Ops.y] + yc*CHUNK_SIZE,p[Ops.z] + zc*CHUNK_SIZE, Chunk.absolute};
		else if(p[identity] == Chunk.absolute)
			return Ops.copy(p);
		else
			return null;
	}
	public int[] toAbsoluteMut(int[] p){
		if(p[identity] == Chunk.local){
			p[Ops.x] = p[Ops.x] + xc*CHUNK_SIZE;
			p[Ops.y] = p[Ops.y] + yc*CHUNK_SIZE;
			p[Ops.z] = p[Ops.z] + zc*CHUNK_SIZE;
			p[identity] = Chunk.absolute;
		}
		if(p[identity] == Chunk.absolute)
			return p;
		else
			return null;
	}
	public static int[] absoluteToChunk(int[] p){
		if(p[identity] == absolute)
			return new int[]{(int)Math.floor((float)p[0]/Chunk.CHUNK_SIZE), (int)Math.floor((float)p[1]/Chunk.CHUNK_SIZE), (int)Math.floor((float)p[2]/Chunk.CHUNK_SIZE), Chunk.chunk};
		else if(p[identity] == Chunk.chunk)
			return Ops.copy(p);
		else
			return null;
	}
	public static int[] absoluteToChunkMut(int[] p){
		if(p[identity] == absolute){
			p[Ops.x] = (int)Math.floor((float)p[0]/Chunk.CHUNK_SIZE);
			p[Ops.y] = (int)Math.floor((float)p[1]/Chunk.CHUNK_SIZE);
			p[Ops.z] = (int)Math.floor((float)p[2]/Chunk.CHUNK_SIZE);
			p[identity] = Chunk.chunk;
		}
		if(p[identity] == Chunk.chunk)
			return p;
		else
			return null;
	}
//	public float[] toAbsolute(float x, float y, float z){
//		return new float[]{x + xc*CHUNK_SIZE, y + yc*CHUNK_SIZE, z + zc*CHUNK_SIZE};
//	}
	
//	public int[] toRelative(int x, int y, int z){
//		return new int[]{x - xc*CHUNK_SIZE, y - yc*CHUNK_SIZE, z - zc*CHUNK_SIZE};
//	}
	public int[] toLocal(int[] p){
		if(p[identity] == Chunk.absolute)
			return new int[]{p[Ops.x] - xc*CHUNK_SIZE, p[Ops.y] - yc*CHUNK_SIZE, p[Ops.z] - zc*CHUNK_SIZE, Chunk.local};
		else if(p[identity] == Chunk.local)
			return Ops.copy(p);
		else
			return null;
	}
	public int[] toLocalMut(int[] p){
		if(p[identity] == Chunk.absolute){
			p[Ops.x] = p[Ops.x] - xc*CHUNK_SIZE;
			p[Ops.y] = p[Ops.y] - yc*CHUNK_SIZE;
			p[Ops.z] = p[Ops.z] - zc*CHUNK_SIZE;
			p[identity] = Chunk.local;
		}
		if(p[identity] == Chunk.local)
			return p;
		else
			return null;
	}
	
}