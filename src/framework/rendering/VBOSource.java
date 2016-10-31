package framework.rendering;

/**
 * Created by eric on 10/30/16.
 */
public enum VBOSource{
	// Each of these ints refer to an index in 'bufferPools'. They are all
	// called 'from<something>' because classes outside the VBO class should not
	// know about how the VBO class handles buffers, all they are doing is
	// telling the VBO who they are, and the VBO uses that information to store
	// their VertexInfo's in the correct buffer pool
	//'fromPlayer' refers to the buffer pool in 'bufferPools' which should
	// only be used by the player.
	 fromPlayer(0),
	// the world gen buffer pool should only be used for world generation. ie
	// when new blocks are being added by the world loader/generator
	fromWorldGen(1),
	// The world clear buffer should only be used for deleting blocks
	fromWorldClear(2);
	
	int bufferIndex;
	private VBOSource(int bufferIndex){
		this.bufferIndex = bufferIndex;
	}
	public int getBufferIndex(){
		return bufferIndex;
	}
}
