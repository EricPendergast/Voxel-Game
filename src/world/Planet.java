package world;

import framework.Blocks;
import framework.Generator;
import framework.Ops;
import framework.OpsMut;
import framework.Runner;
import framework.Timer;
import world.blocks.Block;
public class Planet {
	public static final int x = 0;
	public static final int y = 1;
	public static final int z = 2;
	float[] pos = {0,0,0};
	
	//private Chunk[][][] chunks;
	public ChunkManager chunkManager;
	protected int chunkRadius;
	protected int radius;
	
	protected float gravity;
	
	private CubeNoise noise;
	public Planet(int chunkRadius, int radius, float gravity){
		this.gravity = gravity;
		this.radius = radius;
		this.chunkRadius = chunkRadius;
		noise = new PerlinNoiseAdvanced(radius, 20,40, 1242398);
		chunkManager = new ChunkManager(4, this);

//		chunks = new Chunk[chunkRadius*2][chunkRadius*2][chunkRadius*2];
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y <  chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z] = new Chunk(x-chunkRadius,y-chunkRadius,z-chunkRadius,this);
//					//chunks[x][y][z] = new Chunk(x,y,z,this);
//
//				}
//			}
//		}
	}
	/**
	 * Updates all the chunks
	 * Called 30 times per second
	 */
	public void update(Player player){
		//Timer t = new Timer();
		//t.start();
		//Runner.debug += " PLANET1:" + t.getTime();
		player.update(this);
		//Runner.debug += " 2:" + t.getTime();

		chunkManager.moveCenterTo(player.body.pos);
		//Runner.debug += " 3:" + t.getTime();

		for(int x = 0; x < chunkRadius*2; x++){
			for(int y = 0; y < chunkRadius*2; y++){
				for(int z = 0; z < chunkRadius*2; z++){
					Chunk c = chunkManager.getChunkAtIndex(new int[]{x,y,z});
					if(c != null){
						//does nothing yet
						c.update();
					} 
				}
			}
		}
		//Runner.debug += " 4:" + t.getTime() + "}";
	}
	/**
	 * Sends the polygons to the graphics card
	 * to be called by another thread
	 */
	public void sendPolys(){
		chunkManager.sendPolys();
	}
	/**
	 * Gets the chunk at (x,y,z)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Chunk getChunk(int[] pos){
		try{
			
			return chunkManager.getChunk(pos);
		}catch(ArrayIndexOutOfBoundsException e){return null;}
	}
	/**
	 * Gets the id for the block at (x,y,z)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public byte getBlockID(int x, int y, int z){
		try{
			Chunk c = getChunk(new int[]{x,y,z,Chunk.absolute});//getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
			//System.out.println(c + " " + x + " " + y + " " + z);
			if(c == null)
				return Blocks.UNGENERATED;
			else
				return c.getBlock(new int[]{x,y,z,Chunk.absolute});
		}catch(ArrayIndexOutOfBoundsException e){return Blocks.UNGENERATED;}
	}
	public byte getBlockID(int[] pos){
		return getBlockID(pos[0],pos[1],pos[2]);
	}
	public Block getBlock(int x, int y, int z){
		int id = getBlockID(x,y,z);
		if(id < 0 || id >= Blocks.list.length)
			return null;
		else
			return Blocks.list[id];
	}
	public Block getBlock(int[] pos){
		return getBlock(pos[0],pos[1],pos[2]);
	}

	/**
	 * Generates the block for position (x,y,z)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public byte generate(int x, int y, int z){
//		Generator.last = (int)(x/(y+.000001f)+z);
//		float dist = Math.max(Math.abs(x), Math.max(Math.abs(y),Math.abs(z)));//(float)Math.sqrt(x*x + y*y + z*z);//;
//		if(dist < radius && dist > -1){
//			return (byte)(Math.abs(Generator.getRandomNumber()%10));//(Math.abs(Generator.getRandomNumber()%10)+1);//(byte)Math.abs(Math.sqrt(Math.abs(x*x*y/(z+.00001)))%10);//
//			
//		}else
//			return Blocks.AIR;
		if(noise.generate(x, y, z)){
			Generator.last = (int)(x/(y+.000001f)+z);
			//if((x%16 == 0 && z%16 == 0) || (x%16 == 0 && y%16 == 0) || (z%16 == 0 && y%16 == 0))
			//	return Blocks.UNGENERATED;
			//else
				return 22;//(byte)(Math.abs(Generator.getRandomNumber()%10)+1);//(Math.abs(Generator.getRandomNumber()%10)+1);//(byte)Math.abs(Math.sqrt(Math.abs(x*x*y/(z+.00001)))%10);//
			
		}else
			return Blocks.AIR;
	}
	//basic gravity
//	public float[] getGravityVector(float x, float y, float z){
//		
//		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
//		
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		float volume = (float)(4.0/3*Math.PI*radius*radius*radius);
//		OpsMut.multiply(arr, gravity);//.0001f*volume/length);
//		return arr;// new float[]{0,-.01f,.00001f};
//	}
	
	public float[] getGravityVector(float[] pos){
		int size = radius*2;
		float dist = Ops.distanceBetween(pos,this.pos);
		float[] sq = Ops.multiply(Ops.normalize(getSquareGravityVector(pos)), Math.max(size+5-dist, 0));
		float[] cir = Ops.multiply(Ops.normalize(getRoundGravityVector(pos[0], pos[1] , pos[2])), Math.max(dist-size, 0));
		
		return Ops.add(sq,cir);
	}
	/**
	 * Returns the gravity the planet should enact based on point (x,y,z)
	 * Creates gravity for a cubic surface.
	 * Gravity goes straight down for the large flat areas
	 * and curves around the edges
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
//	public float[] getSquareGravityVector(float x, float y, float z){
//		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
//		float[][] arrs = {	{0,arr[1],arr[2]},
//							{arr[0],0,arr[2]},
//							{arr[0],arr[1],0}};
//		float[] ret = {0,0,0};
//		int numAdds = 0;
//		for(int i = 0; i < 3; i++){
//			float angle = (float)Math.toDegrees(Ops.getAngleBetween(arrs[i], Ops.simplify(arr)));
//			if(angle > 35 && angle <= 45){
//				OpsMut.normalize(arrs[i]);
//				//.57358 is the length of the x component of the vector (0,1) rotated 35 degrees
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//				numAdds++;
//			}
//		}
//		if(numAdds == 2){//if the user is in two sides, the user should actually be in three sides
//			numAdds = 2;
//			ret[0] = ret[1] = ret[2] = 0;
//			arrs = new float[][]{{0,arr[1],arr[2]},{arr[0],0,arr[2]},{arr[0],arr[1],0}};
//			for(int i = 0; i < 3; i++){
//				OpsMut.normalize(arrs[i]);
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//			}
//		}
//		if((ret[Ops.x] != 0 || ret[Ops.y] != 0 || ret[Ops.z] != 0)){
//			//OpsMut.multiply(ret,(float)(1.0/numAdds));
//			OpsMut.normalize(ret);
//			//OpsMut.multiply(ret, gravity);
//			return ret;
//		}
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		//OpsMut.multiply(arr, gravity);
//		return Ops.simplify(arr);
//	}
	public float[] getSquareGravityVector(float[] pos){
		pos = Ops.normalize(pos);
		OpsMut.multiply(pos, this.radius*2f);
		float[] gravCenter = Ops.copy(pos);
		
		int maxIndex = 0;
		for(int i = 1; i < 3; i++)
			maxIndex = Math.abs(pos[maxIndex]) < Math.abs(pos[i]) ? i : maxIndex;
		for(int i = 0; i < 3; i++){
			if(i == maxIndex){
				gravCenter[i] = Math.signum(gravCenter[i])*this.radius;
			}else{
				if(Math.abs(gravCenter[i]) > this.radius){
					gravCenter[i] = Math.signum(gravCenter[i])*this.radius;
				}
			}
		}
		
		OpsMut.multiply(OpsMut.subtract(pos, gravCenter),-1);
		pos[0] += .000001;
		pos[1] += .000001;
		pos[2] += .000001;
		return pos;
	}
	public int[] getBlockGravityVector(int[] pos){
		int max = 0;
		for(int i = 0; i < pos.length; i++)
			if(Math.abs(pos[i]) > Math.abs(pos[max]))
				max = i;
		int[] ret = new int[3];
		ret[max] = max > 0 ? 1 : -1;
		return ret;
	}
	//always pulls toward the center
	public float[] getRoundGravityVector(float x, float y, float z){		
		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
		
		float length = Ops.getLengthsq(arr);
		if(length < 5)
			return new float[]{0,0,0};
		OpsMut.normalize(arr);
		OpsMut.multiply(arr, gravity);
		return arr;
	}
	/**
	 * Sets the block at (x,y,z) to id
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 */
	public void setBlock(int x, int y, int z, byte id){
		Chunk c = getChunk(new int[]{x,y,z,Chunk.absolute});//(int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
		if(c == null)
			return;
		
		c.setBlock(new int[]{x,y,z, Chunk.absolute}, id);
	}
}


//package world;
//
//import framework.Blocks;
//import framework.Generator;
//import framework.Ops;
//import framework.OpsMut;
//import world.blocks.Block;
//public class Planet {
//	public static final int x = 0;
//	public static final int y = 1;
//	public static final int z = 2;
//	float[] pos = {0,0,0};
//	
//	private Chunk[][][] chunks;
//	//public ChunkManager chunkManager;
//	
//	private int chunkRadius;
//	private int radius;
//	
//	private float gravity;
//	public Planet(int chunkRadius, int radius, float gravity){
//		this.gravity = gravity;
//		this.radius = radius;
//		this.chunkRadius = chunkRadius;
//		chunks = new Chunk[chunkRadius*2][chunkRadius*2][chunkRadius*2];
//		//chunkManager = new ChunkManager(1, this);
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z] = new Chunk(x-chunkRadius,y-chunkRadius,z-chunkRadius,this);
//				}
//			}
//		}
//		System.out.println("FINAL");
//	}
//	/**
//	 * Updates all the chunks
//	 */
//	public void update(){
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z].update();
//
//				}
//			}
//		}
//	}
//	/**
//	 * Initializes all of the chunks
//	 */
////	public void init(){
////		for(int x = 0; x < chunkRadius*2; x++){
////			for(int y = 0; y < chunkRadius*2; y++){
////				for(int z = 0; z < chunkRadius*2; z++){
////					chunks[x][y][z].init();
////				}
////			}
////		}
////		for(int x = 0; x < chunkRadius*2; x++){
////			for(int y = 0; y < chunkRadius*2; y++){
////				for(int z = 0; z < chunkRadius*2; z++){
////					chunks[x][y][z].renderInit();
////				}
////			}
////		}
////	}
//	/**
//	 * Gets the chunk that contains the block position (x,y,z) 
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public Chunk getChunkAtPos(int x, int y, int z){
//		try{
//			return getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//		}catch(ArrayIndexOutOfBoundsException e){return null;}
//	}
//	/**
//	 * Gets the chunk at (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public Chunk getChunk(int x, int y, int z){
//		try{
//			
//			return chunks[x+chunkRadius][y+chunkRadius][z+chunkRadius];
//			//return new Chunk(x,y,z, this);
//		}catch(ArrayIndexOutOfBoundsException e){return null;}
//	}
//	/**
//	 * Gets the id for the block at (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public byte getBlockID(int x, int y, int z){
//		try{
//			Chunk c = getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//			
//			if(c == null)
//				return Blocks.AIR;
//			else
//				return c.getBlock(x,y,z);
//		}catch(ArrayIndexOutOfBoundsException e){return Blocks.AIR;}
//	}
//	public Block getBlock(int x, int y, int z){
//		return Blocks.list[getBlockID(x,y,z)];
//	}
//	/**
//	 * Generates the block for position (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public byte generate(int x, int y, int z){
//		Generator.last = (int)(x/(y+.000001f)+z);
//		float dist = Math.max(Math.abs(x), Math.max(Math.abs(y),Math.abs(z)));//(float)Math.sqrt(x*x + y*y + z*z);//;
//		if(dist < radius && dist > -1){
//			return (byte)22;//(byte)(Math.abs(Generator.getRandomNumber()%10)+16);//(Math.abs(Generator.getRandomNumber()%10)+1);//(byte)Math.abs(Math.sqrt(Math.abs(x*x*y/(z+.00001)))%10);//
//		}else
//			return Blocks.AIR;
//	}
//	//basic gravity
////	public float[] getGravityVector(float x, float y, float z){
////		
////		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
////		
////		float length = Ops.getLengthsq(arr);
////		if(length < 5)
////			return new float[]{0,0,0};
////		OpsMut.normalize(arr);
////		float volume = (float)(4.0/3*Math.PI*radius*radius*radius);
////		OpsMut.multiply(arr, gravity);//.0001f*volume/length);
////		return arr;// new float[]{0,-.01f,.00001f};
////	}
//	
//	public float[] getGravityVector(float[] pos){
//		float dist = Ops.distanceBetween(pos,this.pos);
//		float[] sq = Ops.multiply(Ops.normalize(getSquareGravityVector(pos[0],pos[1], pos[2])), Math.max(55-dist, 0));
//		float[] cir = Ops.multiply(Ops.normalize(getRoundGravityVector(pos[0], pos[1] , pos[2])), Math.max(dist-50, 0));
//		
//		return Ops.add(sq,cir);
//	}
//	/**
//	 * Returns the gravity the planet should enact based on point (x,y,z)
//	 * Creates gravity for surface of a cube.
//	 * Gravity goes straight down for the large flat areas
//	 * and curves around the edges
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	private float[] getSquareGravityVector(float x, float y, float z){
//		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
//		float[][] arrs = {	{0,arr[1],arr[2]},
//							{arr[0],0,arr[2]},
//							{arr[0],arr[1],0}};
//		float[] ret = {0,0,0};
//		int numAdds = 0;
//		for(int i = 0; i < 3; i++){
//			float angle = (float)Math.toDegrees(Ops.getAngleBetween(arrs[i], Ops.simplify(arr)));
//			if(angle > 35 && angle <= 45){
//				OpsMut.normalize(arrs[i]);
//				//.57358 is the length of the x component of the vector (0,1) rotated 35 degrees
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//				numAdds++;
//			}
//		}
//		if(numAdds == 2){//if the user is in two sides, the user should actually be in three sides
//			numAdds = 2;
//			ret[0] = ret[1] = ret[2] = 0;
//			arrs = new float[][]{{0,arr[1],arr[2]},{arr[0],0,arr[2]},{arr[0],arr[1],0}};
//			for(int i = 0; i < 3; i++){
//				OpsMut.normalize(arrs[i]);
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//			}
//		}
//		if((ret[Ops.x] != 0 || ret[Ops.y] != 0 || ret[Ops.z] != 0)){
//			//OpsMut.multiply(ret,(float)(1.0/numAdds));
//			OpsMut.normalize(ret);
//			//OpsMut.multiply(ret, gravity);
//			return ret;
//		}
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		//OpsMut.multiply(arr, gravity);
//		return Ops.simplify(arr);
//	}
//	
//	public int[] getBlockGravityVector(int[] pos){
//		int max = 0;
//		for(int i = 0; i < pos.length; i++)
//			if(Math.abs(pos[i]) > Math.abs(pos[max]))
//				max = i;
//		int[] ret = new int[3];
//		ret[max] = max > 0 ? 1 : -1;
//		return ret;
//	}
//	
//	private float[] getRoundGravityVector(float x, float y, float z){		
//		float[] arr = {pos[Ops.x]-x,pos[Ops.y]-y,pos[Ops.z]-z};
//		
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		OpsMut.multiply(arr, gravity);
//		return arr;
//	}
//	/**
//	 * Sets the block at (x,y,z) to id
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @param id
//	 */
//	public void setBlock(int x, int y, int z, byte id){
//		Chunk c = getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//		if(c == null)
//			return;
//		
//		c.setBlock(x,y,z, id);
//	}
//	
//	/**
//	 * Generates or retrieves from file the chunk at pos
//	 * @param pos
//	 * @return
//	 */
////	public Chunk getChunk(int[] pos){
////		
////	}
//}


//package world;
//
//import framework.Blocks;
//import framework.Generator;
//import framework.Ops;
//import framework.OpsMut;
//import world.blocks.Block;
//public class Planet {
//	public static final int x = 0;
//	public static final int y = 1;
//	public static final int z = 2;
//	float[] pos = {0,0,0};
//	
//	private Chunk[][][] chunks;
//	
//	private int chunkRadius;
//	private int radius;
//	
//	private float gravity;
//	public Planet(int chunkRadius, int radius, float gravity){
//		this.gravity = gravity;
//		this.radius = radius;
//		this.chunkRadius = chunkRadius;
//		chunks = new Chunk[chunkRadius*2][chunkRadius*2][chunkRadius*2];
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z] = new Chunk(x-chunkRadius,y-chunkRadius,z-chunkRadius,this);
//					//chunks[x][y][z] = new Chunk(x,y,z,this);
//
//				}
//			}
//		}
//	}
//	/**
//	 * Updates all the chunks
//	 */
//	public void update(){
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z].update();
//
//				}
//			}
//		}
//	}
//	/**
//	 * Initializes all of the chunks
//	 */
//	public void init(){
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z].init();
//				}
//			}
//		}
//		for(int x = 0; x < chunkRadius*2; x++){
//			for(int y = 0; y < chunkRadius*2; y++){
//				for(int z = 0; z < chunkRadius*2; z++){
//					chunks[x][y][z].updatePolys();
//				}
//			}
//		}
//	}
//	/**
//	 * Gets the chunk that contains the block position (x,y,z) 
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public Chunk getChunkAtPos(int x, int y, int z){
//		try{
//			return getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//		}catch(ArrayIndexOutOfBoundsException e){return null;}
//	}
//	/**
//	 * Gets the chunk at (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public Chunk getChunk(int x, int y, int z){
//		try{
//			
//			return chunks[x+chunkRadius][y+chunkRadius][z+chunkRadius];
//		}catch(ArrayIndexOutOfBoundsException e){return null;}
//	}
//	/**
//	 * Gets the id for the block at (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public byte getBlockID(int x, int y, int z){
//		try{
//			Chunk c = getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//			
//			if(c == null)
//				return Blocks.AIR;
//			else
//				return c.getBlock(x,y,z);
//		}catch(ArrayIndexOutOfBoundsException e){return Blocks.AIR;}
//	}
//	public Block getBlock(int x, int y, int z){
//		return Blocks.list[getBlockID(x,y,z)];
//	}
//	/**
//	 * Generates the block for position (x,y,z)
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public byte generate(int x, int y, int z){
//		Generator.last = (int)(x/(y+.000001f)+z);
//		float dist = Math.max(Math.abs(x), Math.max(Math.abs(y),Math.abs(z)));//(float)Math.sqrt(x*x + y*y + z*z);//;
//		if(dist < radius && dist > -1){
//			return (byte)22;//(byte)(Math.abs(Generator.getRandomNumber()%10)+16);//(Math.abs(Generator.getRandomNumber()%10)+1);//(byte)Math.abs(Math.sqrt(Math.abs(x*x*y/(z+.00001)))%10);//
//		}else
//			return Blocks.AIR;
//	}
//	//basic gravity
////	public float[] getGravityVector(float x, float y, float z){
////		
////		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
////		
////		float length = Ops.getLengthsq(arr);
////		if(length < 5)
////			return new float[]{0,0,0};
////		OpsMut.normalize(arr);
////		float volume = (float)(4.0/3*Math.PI*radius*radius*radius);
////		OpsMut.multiply(arr, gravity);//.0001f*volume/length);
////		return arr;// new float[]{0,-.01f,.00001f};
////	}
//	
//	public float[] getGravityVector(float[] pos){
//		float dist = Ops.distanceBetween(pos,this.pos);
//		float[] sq = Ops.multiply(Ops.normalize(getSquareGravityVector(pos[0],pos[1], pos[2])), Math.max(55-dist, 0));
//		float[] cir = Ops.multiply(Ops.normalize(getRoundGravityVector(pos[0], pos[1] , pos[2])), Math.max(dist-50, 0));
//		
//		return Ops.add(sq,cir);
//	}
//	/**
//	 * Returns the gravity the planet should enact based on point (x,y,z)
//	 * Creates gravity for a cubic surface.
//	 * Gravity goes straight down for the large flat areas
//	 * and curves around the edges
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public float[] getSquareGravityVector(float x, float y, float z){
//		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
//		float[][] arrs = {	{0,arr[1],arr[2]},
//							{arr[0],0,arr[2]},
//							{arr[0],arr[1],0}};
//		float[] ret = {0,0,0};
//		int numAdds = 0;
//		for(int i = 0; i < 3; i++){
//			float angle = (float)Math.toDegrees(Ops.getAngleBetween(arrs[i], Ops.simplify(arr)));
//			if(angle > 35 && angle <= 45){
//				OpsMut.normalize(arrs[i]);
//				//.57358 is the length of the x component of the vector (0,1) rotated 35 degrees
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//				numAdds++;
//			}
//		}
//		if(numAdds == 2){//if the user is in two sides, the user should actually be in three sides
//			numAdds = 2;
//			ret[0] = ret[1] = ret[2] = 0;
//			arrs = new float[][]{{0,arr[1],arr[2]},{arr[0],0,arr[2]},{arr[0],arr[1],0}};
//			for(int i = 0; i < 3; i++){
//				OpsMut.normalize(arrs[i]);
//				OpsMut.subtract(arrs[i], new float[]{	.57358f*((i==0?0:(arrs[i][Ops.x]>0?1:-1))),
//														.57358f*((i==1?0:(arrs[i][Ops.y]>0?1:-1))),
//														.57358f*((i==2?0:(arrs[i][Ops.z]>0?1:-1)))	});
//				OpsMut.normalize(arrs[i]);
//				//OpsMut.multiply(arrs[i], gravity);
//				OpsMut.add(ret, arrs[i]);
//			}
//		}
//		if((ret[Ops.x] != 0 || ret[Ops.y] != 0 || ret[Ops.z] != 0)){
//			//OpsMut.multiply(ret,(float)(1.0/numAdds));
//			OpsMut.normalize(ret);
//			//OpsMut.multiply(ret, gravity);
//			return ret;
//		}
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		//OpsMut.multiply(arr, gravity);
//		return Ops.simplify(arr);
//	}
//	
//	public int[] getBlockGravityVector(int[] pos){
//		int max = 0;
//		for(int i = 0; i < pos.length; i++)
//			if(Math.abs(pos[i]) > Math.abs(pos[max]))
//				max = i;
//		int[] ret = new int[3];
//		ret[max] = max > 0 ? 1 : -1;
//		return ret;
//	}
//	
//	public float[] getRoundGravityVector(float x, float y, float z){		
//		float[] arr = {pos[this.x]-x,pos[this.y]-y,pos[this.z]-z};
//		
//		float length = Ops.getLengthsq(arr);
//		if(length < 5)
//			return new float[]{0,0,0};
//		OpsMut.normalize(arr);
//		OpsMut.multiply(arr, gravity);
//		return arr;
//	}
//	/**
//	 * Sets the block at (x,y,z) to id
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @param id
//	 */
//	public void setBlock(int x, int y, int z, byte id){
//		Chunk c = getChunk((int)Math.floor(x/16.0),(int)Math.floor(y/16.0),(int)Math.floor(z/16.0));
//		if(c == null)
//			return;
//		
//		c.setBlock(x,y,z, id);
//	}
//}
