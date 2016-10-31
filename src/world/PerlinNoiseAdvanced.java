package world;

import framework.Generator;

public class PerlinNoiseAdvanced extends CubeNoise{
	PerlinNoise perlin1;
	PerlinNoise perlin2;
	Cave cave = new Cave();
	float[][] rot;
	int layers;
	public PerlinNoiseAdvanced(int radius, int height, int width, int seed){
		super(radius, height, width, seed);
		perlin1 = new PerlinNoise(radius, height, width, seed);
		perlin2 = new PerlinNoise(radius, height, width, seed*seed);
		float angle = 50;
		angle = (float)(Math.toRadians(angle));
		rot = new float[][]{{(float)Math.cos(angle), -(float)Math.sin(angle)}, {(float)Math.sin(angle), (float)Math.cos(angle)}};
		layers = 1;
	}
	public PerlinNoiseAdvanced(int radius, int height, int width, int layers, int seed){
		this(radius,height,width,seed);
		this.layers = layers;
	}
	/**
	 * Returns true if the block is occupied, false if not
	 */
	public boolean generate(int x, int y, int z){
		if(cave.isCave(x, y, z))
			return false;
		int[] pos = {x,y,z};
		int maxIndex = 0;
		for(int i = 0; i < 3; i++)
			if(Math.abs(pos[maxIndex]) < Math.abs(pos[i]))
				maxIndex = i;
		
		int nx = pos[(maxIndex+1)%3];
		int ny = pos[(maxIndex+2)%3];
//		int oldSeed = seed;
//		float a = generate2D(nx,ny);
//		seed*= 2;
//		a += generate2D(nx*16,ny*16)/4;
//		seed = oldSeed;
		if(Math.floor(Math.abs(pos[maxIndex])) > radius + height)
			return false;
		return Math.floor(Math.abs(pos[maxIndex])) < radius || Math.floor(Math.abs(pos[maxIndex])) < radius + genHeight(nx,ny);
	}
	public float genHeight(int x, int y){
		float avg = 0;
		int n = 0;
		for(int i = -2; i <= 2; i++){
			for(int j = -2; j <= 2; j++){
				//if((i == 0) && (j == 0)){
					avg += generate2D(x+i,y+j);
					n++;
				//}
			}
		}
		avg/=n;
		int oldSeed = seed;
		float a = avg; //generate2D(x,y);
		seed*= 2;
		//a += generate2D(x*6,y*6)/16;
		seed = oldSeed;
		return a;
	}

	public float generate2D(float x, float y){
		float a = perlin1.generate2D(x, y);
		float x2 = x*rot[0][0] + y*rot[0][1];
		float y2 = x*rot[1][0] + y*rot[1][1];
		float b = perlin2.generate2D(x2, y2);

		return (a + b)/2;
	}
	
}


class Cave{
	int nodeDensity = 20;
	int connectDensity = 1;
	int prime1 = 179424691;
	int prime2 = 179425601;
	int prime3 = 179426231;
	public boolean isCave(int x, int y, int z){
		int nx = x/nodeDensity*nodeDensity;
		int ny = y/nodeDensity*nodeDensity;
		int nz = z/nodeDensity*nodeDensity;
		
		int rand = genPoint(nx,ny,nz);
		int[] nodePos = {rand%prime1,rand%prime2,rand%prime3};
		for(int i = 0; i < 3; i ++)
			nodePos[i] = nodePos[i]%nodeDensity;
		
		int dist = (int)Math.sqrt(Math.pow(x%nodeDensity-nodePos[0],2) + Math.pow(y%nodeDensity-nodePos[1],2) + Math.pow(z%nodeDensity-nodePos[2],2));
		return dist < 6;
	}
	
	public int genPoint(int x, int y, int z){
		Generator.last = (int)Math.floor(x + y + z + x*y*y + y*z*z);
		return Generator.generate();
	}
}