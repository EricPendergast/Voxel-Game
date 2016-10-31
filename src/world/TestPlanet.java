package world;

import framework.Blocks;

public class TestPlanet extends Planet{

	public TestPlanet(int chunkRadius, int radius, float gravity) {
		super(chunkRadius, radius, gravity);
	}
	public byte generate(int x, int y, int z){
		float dist = Math.max(Math.abs(x), Math.max(Math.abs(y),Math.abs(z)));
		if(dist < radius && dist > -1){
			return (byte)22;
		}else
			return Blocks.AIR;
	}
	
	public void test(){
		float[] pos = new float[]{0,0,0};
		System.out.println("pos " + pos[0] + " " + pos[1] + " " + pos[2]);
		//chunkManager.generate();
		//chunkManager.getChunkAtLocation(pos).setBlockNoRenderUpdate((int)pos[0], (int)pos[1], (int)pos[2], (byte)3);
		chunkManager.moveCenterTo(new float[]{16,0,0});
		//Chunk cur = chunkManager.getChunkAtLocation(pos);
		//System.out.println(cur);
		System.out.println(this.getBlock(0, 0, 0).id);
		//System.out.println(chunkManager.getChunkAtLocation(pos).getBlock((int)pos[0], (int)pos[1], (int)pos[2]));
	}
}
