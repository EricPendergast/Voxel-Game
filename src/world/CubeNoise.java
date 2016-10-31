package world;

import java.util.Random;

import framework.Generator;

public class CubeNoise {
	Random rand = new Random();
	int radius;
	int width;
	int height;
	int seed;
	public CubeNoise(int radius, int height, int width, int seed){
		this.radius = radius;
		this.width = width;
		this.height = height;
		this.seed = seed;
	}
	
	public boolean generate(int x, int y, int z){
		int[] pos = {x,y,z};
		int maxIndex = 0;
		for(int i = 0; i < 3; i++)
			if(Math.abs(pos[maxIndex]) < Math.abs(pos[i]))
				maxIndex = i;
		
		return Math.abs(pos[maxIndex]) < radius + genPoint(pos[(maxIndex+1)%3], pos[(maxIndex+2)%3], maxIndex);
	}
	
	/**
	 * Returns the height of the world at a given 2D point
	 * @param nx
	 * @param ny
	 * @param extra for extra information. Make zero to have no effect
	 * @return
	 */
	public int genPoint(int nx, int ny, int extra){
		nx = (int)Math.floor((float)nx/width);
		ny = (int)Math.floor((float)ny/width);
		
		Generator.last = (int)Math.floor(Math.abs((nx + ny + nx*ny*ny)*(seed*(extra+1) + extra)));
		return Math.abs(Generator.getRandomNumber())%height;
	}
}

