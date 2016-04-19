package world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import framework.Ops;
import framework.Runner;
import framework.Timer;
import framework.queues.ChunkNode;
import framework.queues.ConcurrentQueue;
import framework.rendering.BlockRenderer;

public class ChunkManager {
	/**
	 * The pseudo-radius of chunks being rendered around the player.
	 * The chunks around the player are rendered in a cube, 'radius' is 
	 * the number of chunks away from the player the furthest chunk is,
	 * not including the chunk the player is currently in.
	 */
	private int radius;
	/**
	 * The size of 'chunks' in all 3 directions
	 */
	private int size;
	/**
	 * The planet that contains this chunk manager
	 */
	private Planet planet;
	/**
	 * Holds all the chunks that are being updated and rendered
	 */
	private ChunkContainer chunks;
	/**
	 * The location of the player
	 */
	private int[] center;
	/**
	 * The chunks that are no longer in 'chunks' but are still being rendered.
	 * These chunks are removed from the graphics card in renderOptimized().
	 */
	//private LinkedList<Chunk> chunksToClear;
	private ConcurrentQueue<ChunkNode> chunksToClear;
	/**
	 * Contains a list of all the coordinates that can be rendered
	 */
	private ArrayList<SpecialCoord> allCoords;
	
	private boolean allGenerated = false;
	public ChunkManager(int radius, Planet p){
		this.radius = radius;
		this.planet = p;
		size = radius*2+1;
		chunks = new ChunkContainer(radius, p);
		center = new int[]{0,0,0,Chunk.absolute};
		chunksToClear = new ConcurrentQueue<ChunkNode>();
		{
			allCoords = new ArrayList<SpecialCoord>((size)*(size)*size);
			for(int i = 0; i < size+2; i++)
				for(int j = 0; j < size+2; j++)
					for(int k = 0; k < size+2; k++)
						allCoords.add(new SpecialCoord(i-radius,j-radius,k-radius));
			Collections.sort(allCoords);
		}
	}
	
//	/**
//	 * renders all the chunks that are generated but not rendered
//	 */
//	public void render(){
//		for(int x = 0; x < size; x++){
//			for(int y = 0; y < size; y++){
//				for(int z = 0; z < size; z++){
//					Chunk c = chunks.get(x,y,z);
//					if(c != null && !c.isRendered()){
//						c.renderInit();
//					}
//				}
//			}
//		}
//	}
	/**
	 * Renderes non-rendered chunks, and clears chunks that should no longer be rendered
	 * @param timeLeft	in milliseconds, the time this method takes to run will always try to be below this
	 */
//	public void render(int timeLeft){
//		Timer t = new Timer();
//		t.start();
//
//		
//		Timer debug = new Timer();
//		debug.start();
//		Timer timer = new Timer();
//		timer.start();
////		for(int j = 0; j < (size)*(size)*(size); j++){
////			SpecialCoord c = allCoords.get(j);
////			if(!generateChunk(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius,(int)(timeLeft-timer.getTime())))
////				return;
////			for(int i = 1; i <= 7; i++){
////				SpecialCoord d = allCoords.get(i);
////				if(!generateChunk(c.get(0)+d.get(0)+radius,c.get(1)+d.get(1)+radius,c.get(2)+d.get(2)+radius,(int)(timeLeft-timer.getTime())))
////					return;
////			}
////			if(!renderChunk(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius,(int)(timeLeft-timer.getTime())))
////				return;
////		}
//		SpecialCoord c;
//		SpecialCoord d;
//		//iterates through 'chunks' in order of distance and renders the chunks that are both generated and surrounded by generated chunks
//		for(int j = 0; j < (size)*(size)*(size); j++){
//			c = allCoords.get(j);
//			if(timer.getTime() > timeLeft)
//				return;
//			if(!isGenerated(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius))
//				return;
//			for(int i = 1; i <= 7; i++){
//				d = allCoords.get(i);
//				if(!isGenerated(c.get(0)+d.get(0)+radius,c.get(1)+d.get(1)+radius,c.get(2)+d.get(2)+radius))
//					return;
//			}
//			if(!renderChunk(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius,(int)(timeLeft-timer.getTime())))
//				return;
//			
//			//if(chunksToClear.size() > 0 && chunksToClear.get(0).clearGraphicsPart((int)(timeLeft-rTimer.getTime())))
//			//	chunksToClear.remove();
//		}
//	}
	/**
	 * 
	 */
	public synchronized void sendPolys(){
		while(chunksToClear.size() > 0){
			Chunk chu = null;
			ChunkNode cNode = null;
			cNode = chunksToClear.peek();
			if(cNode != null)
				chu = cNode.chunk;
			if(chu != null && chu.clearGraphicsPart()){
				chunksToClear.pop();
			}
		}
		SpecialCoord c;
		SpecialCoord d;
		//iterates through 'chunks' in order of distance and renders the chunks that are both generated and surrounded by generated chunks
		for(int j = 0; j < (size)*(size)*(size); j++){
			if(BlockRenderer.pause)
				continue;
			c = allCoords.get(j);
			if(!isGenerated(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius))
				return;
			for(int i = 1; i <= 7; i++){
				d = allCoords.get(i);
				if(!isGenerated(c.get(0)+d.get(0)+radius,c.get(1)+d.get(1)+radius,c.get(2)+d.get(2)+radius))
					return;
			}
			if(!renderChunk(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius))
				return;
			
			
		}
	}
	/**
	 * generates all the blocks in all the chunks in order of distance
	 * @throws InterruptedException		if interrupted
	 */
	public void generate(){// throws InterruptedException{
		Timer timer = new Timer();
		timer.start();
		SpecialCoord c;
		SpecialCoord d;
		for(int j = 0; j < size*size*size; j++){
			
			c = allCoords.get(j);
			generateChunk(c.get(0)+radius,c.get(1)+radius,c.get(2)+radius);
			for(int i = 1; i <= 7; i++){
				d = allCoords.get(i);
				generateChunk(c.get(0)+d.get(0)+radius,c.get(1)+d.get(1)+radius,c.get(2)+d.get(2)+radius);
			}
			//it resets at intervals so that if a close chunk is skipped, the loop can come back to it
			if(timer.getTime() > 50){
				j = 0;
				timer.reset();
			}
			//if(Thread.interrupted())
			//	throw new InterruptedException();
		}
	}
//	/**
//	 * Renders unrendered chunks and clears already rendered chunks that are no longer in range
//	 */
//	Timer rTimer = new Timer();
//	public boolean renderChunk(int x, int y, int z, int timeLeft){
//		
//		rTimer.start();
//		if(rTimer.getTime() > timeLeft){
//			return false;
//		}
//		if(!chunks.isInBounds(x,y,z))
//			return true;
//		Chunk c = chunks.get(x,y,z);
//		if(c == null)
//			return true;
////		if(c != null && !c.isGenerated())
////			c.generatePart((int)(timeLeft-rTimer.getTime()));
////		if(rTimer.getTime() > timeLeft)
////			return false;
//		
//		if(c != null && !c.isRendered()){
//			c.renderPart((int)(timeLeft-rTimer.getTime()));
//		}
//		if(rTimer.getTime() > timeLeft){
//			return false;
//		}
//		
//		return true;
//	}
	//return true : fully rendered
	//return false : ran out of buffer space
	public boolean renderChunk(int x, int y, int z){
		if(!chunks.isInBounds(x,y,z))
			return true;
		Chunk c = chunks.get(x,y,z);
		if(c == null)
			return true;
		
		if(!c.isRendered()){
			if(c.render())
				return true;
			else
				return false;
		}
		
		return true;
	}
	/**
	 * Generates the blocks in a given chunk
	 */
	//Timer gTimer = new Timer();
	public void generateChunk(int x, int y, int z){
		//gTimer.start();
		//if(gTimer.getTime() > timeLeft){
		//	return false;
		//}
		if(!chunks.isInBounds(new int[]{x,y,z}))
			return;
		Chunk c = chunks.get(x,y,z);
		if(c == null){
			int[] chunkShift = Chunk.absoluteToChunk(center);
			c = new Chunk(x + chunkShift[0]-radius, y + chunkShift[1]-radius, z + chunkShift[2]-radius, planet);
			chunks.put(c, x, y, z);
		}
		if(!c.isGenerated())
			c.generate();
		//if(gTimer.getTime() > timeLeft)
		//	return false;
		//else
		//	return true;
	}
	public boolean isGenerated(int x, int y, int z){
		if(!chunks.isInBounds(new int[]{x,y,z}))
			return false;
		Chunk c = chunks.get(x,y,z);
		if(c == null)
			return false;
		return c.isGenerated();
	}
	/**
	 * Shifts all the chunks in 'chunks' so that 'pos' is in the center chunk
	 * @param pos
	 */
	public void moveCenterTo(float[] pos){
		Timer dt = new Timer();
		dt.start();
		Runner.debug += " CMAN1:" + dt.getTime();

		//The world position of the chunk at the center of the chunk matrix
		int[] centerChunk = new int[3];
		//How many chunks to shift in the x,y,z direction
		int[] chunkShift = new int[3];
		//whether or not chunkShift == (0,0,0)
		boolean notZero = false;
		for(int i = 0; i < 3; i++){
			chunkShift[i] = -((int)Math.floor(pos[i]/Chunk.CHUNK_SIZE) - (int)Math.floor((float)center[i]/Chunk.CHUNK_SIZE));
			centerChunk[i] = (int)Math.floor(pos[i]/Chunk.CHUNK_SIZE);
			notZero = chunkShift[i] != 0 ? true : notZero;
		}
		Runner.debug += " 2:" + dt.getTime();
		
		center = new int[]{(int)Math.floor(pos[0]),(int)Math.floor(pos[1]),(int)Math.floor(pos[2]),Chunk.absolute};//Ops.copy(pos);
		
		if(notZero){
			Runner.debug += " 3:" + dt.getTime();
			LinkedList<Chunk> list = chunks.shift(chunkShift);
			while(list.size() > 0){
				chunksToClear.push(new ChunkNode(list.pop()));
			}
			Runner.debug += " 4:" + dt.getTime();
////			for(int x = 0; x < size; x++){
////				for(int y = 0; y < size; y++){
////					for(int z = 0; z < size; z++){
////						if(chunks.get(x,y,z) == null){
////							//debug += x + " " + y + " " + z + " " + t.getTime() + "\n";
////							//The world position of the chunk that will be at index x,y,z
////							int[] chunkPos = {x + centerChunk[0]-radius, y + centerChunk[1]-radius, z + centerChunk[2]-radius};
////							Chunk c = new Chunk(chunkPos[0], chunkPos[1], chunkPos[2], planet);
////							chunks.put(c, x, y, z);
////						}
////					}
////				}
////			}
			
		}
		Runner.debug += " 5:" + dt.getTime() + "}";
	}
	
	public Chunk getChunk(int[] loc){
		loc = Chunk.absoluteToChunk(loc);
		int[] index = new int[3];//loc - center
		for(int i = 0; i < 3; i++){
			index[i] = loc[i] - (int)Math.floor((float)center[i]/Chunk.CHUNK_SIZE) + radius;
		}
		if(chunks.isInBounds(index)){
			return chunks.get(index);
		}else{
			return null;
		}
//		boolean zero = true;
//		for(int i = 0; i < 3; i++){
//			if(index[i] != 0)
//				zero = false;		
//		}
//		if(zero){
//			return chunk;
//		}else
//			return null;
	}
	
	//public Chunk getChunkAtLocation(float[] loc){
	//	return getChunk(new int[]{(int)Math.floor(loc[0]/Chunk.CHUNK_SIZE), (int)Math.floor(loc[1]/Chunk.CHUNK_SIZE), (int)Math.floor(loc[2]/Chunk.CHUNK_SIZE),Chunk.chunk});
	//}
	//public int[] toChunkPos(float[] loc){
	//	return new int[]{(int)Math.floor(loc[0]/Chunk.CHUNK_SIZE), (int)Math.floor(loc[1]/Chunk.CHUNK_SIZE), (int)Math.floor(loc[2]/Chunk.CHUNK_SIZE)};
	//}
	public Chunk getChunkAtIndex(int[] loc){
//		if(loc[0] == 0 && loc[1] == 0 && loc[2] == 0)
//			return chunk;
//		else
//			return null;
		if(chunks.isInBounds(loc))
			return chunks.get(loc);
		else
			return null;
	}
}

//data structure that holds the chunks for ChunkManager
class ChunkContainer{
	private int radius;
	private Chunk[][][] chunks;
	private Planet planet;
	public ChunkContainer(int radius, Planet p){
		this.radius = radius;
		chunks = new Chunk[radius*2+1][radius*2+1][radius*2+1];
		planet = p;
	}
	/**
	 * Shifts all the values in the direction of sh.
	 * Values that are shifted outside the array will be removed
	 */
	public LinkedList<Chunk> shift(int[] sh){
		LinkedList<Chunk> toRemove = new LinkedList<Chunk>();
		Chunk[][][] newChunks = new Chunk[chunks.length][chunks[0].length][chunks[0][0].length];
		for(int x = 0; x < chunks.length; x++){
			for(int y = 0; y < chunks[0].length; y++){
				for(int z = 0; z < chunks[0][0].length; z++){
					//newChunks[(x,y,z)+sh] = chunks[x][y][z]
					if(x+sh[0] < chunks.length && x+sh[0] >= 0 && y+sh[1] < chunks[0].length && y+sh[1] >= 0 && z+sh[2] < chunks[0][0].length && z+sh[2] >= 0){
						newChunks[x+sh[0]][y+sh[1]][z+sh[2]] = chunks[x][y][z];
					}else if(chunks[x][y][z] != null)
						toRemove.add(0,chunks[x][y][z]);
				}
			}
		}
		
		chunks = newChunks;
		
		return toRemove;
	}
	
	public void put(Chunk c, int[] pos){
		chunks[pos[0]][pos[1]][pos[2]] = c;
	}
	public void put(Chunk c, int x, int y, int z){
		chunks[x][y][z] = c;
	}
	
	public Chunk get(int[] pos){
		return chunks[pos[0]][pos[1]][pos[2]];
	}
	public Chunk get(int x, int y, int z){
		return chunks[x][y][z];
	}
	
	public boolean isInBounds(int[] pos){
		for(int i = 0; i < 3; i++)
			if(pos[i] >= chunks.length || pos[i] < 0)
				return false;
		return true;
	}
	public boolean isInBounds(int x, int y, int z){
		return x < chunks.length && x >= 0 && y < chunks.length && y >= 0 && z < chunks.length && z >= 0;
	}
}

class SpecialCoord implements Comparable{
	int[] pos;
	public SpecialCoord(int x, int y, int z){
		pos = new int[]{x,y,z};
	}
	public int get(int ind){
		return pos[ind];
	}
	public int compareTo(Object coord){
		if(!(coord instanceof SpecialCoord))
			return 0;
		int[] posb = ((SpecialCoord)coord).pos;
		int distsq = pos[0]*pos[0] + pos[1]*pos[1] + pos[2]*pos[2];
		int distsqB = posb[0]*posb[0] + posb[1]*posb[1] + posb[2]*posb[2];
//		int distsq = Math.abs(pos[0]) + Math.abs(pos[1]) + Math.abs(pos[2]);
//		int distsqB = Math.abs(posb[0]) + Math.abs(posb[1]) + Math.abs(posb[2]);
		return distsq - distsqB;
	}
	
	public String toString(){
		return "[" + pos[0] + ", " + pos[1] + ", " + pos[2] + "]";
	}
}