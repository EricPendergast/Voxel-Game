package world;

import java.util.Arrays;

import framework.Generator;

public class PerlinNoise extends CubeNoise {
	public static float[] cos;
	static{
		cos = new float[1000];
		float inc = (float)(2*Math.PI/cos.length);
		float total = 0;
		for(int i = 0; i < cos.length; i++){
			cos[i] = (float)Math.cos(total);
			total += inc;
		}
	}
	public PerlinNoise(int radius, int height, int width, int seed) {
		super(radius, height, width, seed);
	}
	
	
//	public boolean generate(int x, int y, int z){
//		int[] pos = {Math.abs(x)*10 + 0,Math.abs(y)*10 + 1,Math.abs(z)*10 + 2};
//		Arrays.sort(pos);
//		int[] npos = {x,y,z};
//		
//		int res = npos[pos[2]%10];
//		npos[pos[2]%10] = npos[(pos[2]%10+1)%3];
//		npos[(pos[2]%10+1)%3] = res;
//		
//		//System.out.println(pos[0] + " " + pos[1] + " " + pos[2] + " " + generate2D(pos[0],pos[1]));
//		return npos[2] < radius + generate2D(npos[0],npos[1]);
//	}
	
	//This method is slow
	public boolean generate(int x, int y, int z){
		int[] pos = {x,y,z};
		int maxIndex = 0;
		for(int i = 0; i < 3; i++)
			if(Math.abs(pos[maxIndex]) < Math.abs(pos[i]))
				maxIndex = i;
		
		int nx = pos[(maxIndex+1)%3];
		int ny = pos[(maxIndex+2)%3];
		
		//int ret = radius + generate2D(nx,ny);
		return Math.abs(pos[maxIndex]) < radius + Math.floor(generate2D(nx,ny));//generate2D(nx,ny);
	}
	public float generate2D(float x, float y){
		interval = width;
		double h1 = interpolate(genPoint(floor(x),floor(y)), width, genPoint(ceil(x),floor(y)), x - floor(x));

		double h2 = interpolate(genPoint(floor(x),ceil(y)), width, genPoint(ceil(x),ceil(y)), x - floor(x));
		return (int)Math.floor(interpolate(h1, width, h2, y - floor(y)));
	}
	
//	public int generate3D(int x, int y, int z){
//		interval = width;
//		double h1 = 0;
//		{
//			double a = interpolate(genPoint3D(floor(x),floor(y),floor(z)), width, genPoint3D(ceil(x),floor(y),floor(z)), x%width);
//			//System.out.println(h1);
//			double b = interpolate(genPoint3D(floor(x),ceil(y),floor(z)), width, genPoint3D(ceil(x),ceil(y),floor(z)), x%width);
//			h1 = (int)Math.floor(interpolate(a, width, b, y%width));
//		}
//		double h2 = 0;
//		{
//			double a = interpolate(genPoint3D(floor(x),floor(y),ceil(z)), width, genPoint3D(ceil(x),floor(y),ceil(z)), x%width);
//			//System.out.println(h1);
//			double b = interpolate(genPoint3D(floor(x),ceil(y),ceil(z)), width, genPoint3D(ceil(x),ceil(y),ceil(z)), x%width);
//			h2 = (int)Math.floor(interpolate(a, width, b, y%width));
//		}
//		return (int)Math.floor(interpolate(h1, width, h2, z%width));
//	}
	//sine interpolation
//	public double interpolate(double a, double w, double b, double p){
//		
//		double i = ((((a-b)/2)*cos((float)(Math.PI/w*p))+(a+b)/2));
//		//if(p < 1)
//		//	return 255;
//		return i;
//		
//	}
	//cubic interpolation
	public double interpolate(double a, double w, double b, double p){
		//TODO Turn Math.cos into an array
		double t = p/w;
		return (b-a)*t*t*t*(6*t*t-15*t+10) + a;
		//double i = ((((a-b)/2)*cos((float)(Math.PI/w*p))+(a+b)/2));
		//if(p < 1)
		//	return 255;
		//return i;
		
	}
	public int genPoint(int x, int y){
		Generator.last = (int)Math.floor((x + y + x*y*y)*(seed));
		int f = Generator.generate();
		//System.out.println((double)f/Integer.MAX_VALUE);
		return Math.abs(f%height);
	}
//	public int genPoint3D(int x, int y, int z){
//		Generator.last = (int)Math.floor(Math.abs((x + y + z + x*y*y + z*x*x)*(seed)));
//		return Math.abs(Generator.generate()%height);
//	}
	int interval = 20;
	public int floor(float a){
		return (int)fastfloor(a/interval)*interval;
	}
	
	public int ceil(float a){
		return (int)fastceil(a/interval)*interval;
	}
	
	public int fastfloor(float a){
		return a>0 ? (int)a : (int)a-1;
	}
	public int fastceil(float a){
		return a>0 ? (int)a+1 : (int)a;
	}
	public float cos(float a){
		a /= 2*Math.PI;
		a = a % (2*(float)Math.PI);
		a *= cos.length;
		return cos[Math.round(a)];
	}
}
