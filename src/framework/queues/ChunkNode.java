package framework.queues;

import world.Chunk;

public class ChunkNode extends ConcurrentNode {
	public Chunk chunk;
	public ChunkNode(Chunk c){
		chunk = c;
	}
}
