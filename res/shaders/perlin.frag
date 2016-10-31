varying vec3 vertex;
uniform float val;

float perlinNoise(float x, float y, float z);
float genPoint3D(float x, float y, float z);
float interpolate(float a, float w, float b, float p);
float floor2(float a);
int fastfloor(float a);
float ceil2(float a);
int fastceil(float a);

void main(){
	vec3 pos = vertex;//normalize(vertex);
	//pos *= val;
	gl_FragColor = vec4(perlinNoise(pos.x+val,pos.y+val,pos.z+val),0,0,1);
}

float perlinNoise(float x, float y, float z){
	float width = .1;
	float xMod = mod(x,width);
	float yMod = mod(y,width);
	float zMod = mod(z,width);
	
	float h1 = 0;
	
		float a = interpolate(genPoint3D(floor2(x),floor2(y),floor2(z)), width, genPoint3D(ceil2(x),floor2(y),floor2(z)), xMod);
		float b = interpolate(genPoint3D(floor2(x),ceil2(y),floor2(z)), width, genPoint3D(ceil2(x),ceil2(y),floor2(z)), xMod);
		h1 = interpolate(a, width, b, yMod);
	
	float h2 = 0;
	
		a = interpolate(genPoint3D(floor2(x),floor2(y),ceil2(z)), width, genPoint3D(ceil2(x),floor2(y),ceil2(z)), xMod);
		b = interpolate(genPoint3D(floor2(x),ceil2(y),ceil2(z)), width, genPoint3D(ceil2(x),ceil2(y),ceil2(z)), xMod);
		h2 = interpolate(a, width, b, yMod);
	
	return interpolate(h1, width, h2, zMod);
}

float genPoint3D(float x, float y, float z){
	return mod((x + y + z + x*y*y + z*x*x)*34626427, 10);
}
float interpolate(float a, float w, float b, float p){
	float t = p/w;
	return (b-a)*t*t*t*(6*t*t-15*t+10) + a;
}


float floor2(float a){
	float interval = .1;
	return fastfloor(a/interval)*interval;
}

float ceil2(float a){
	float interval = .1;
	return fastceil(a/interval)*interval;
}

int fastfloor(float a){
	return a>0 ? int(a) : int(a)-1;
}

int fastceil(float a){
	return a>0 ? int(a)+1 : int(a);
}

/*
public int generate3D(int x, int y, int z){
	double h1 = 0;
	{
		double a = interpolate(genPoint3D(floor(x),floor(y),floor(z)), width, genPoint3D(ceil(x),floor(y),floor(z)), x%width);
		//System.out.println(h1);
		double b = interpolate(genPoint3D(floor(x),ceil(y),floor(z)), width, genPoint3D(ceil(x),ceil(y),floor(z)), x%width);
		h1 = (int)Math.floor(interpolate(a, width, b, y%width));
	}
	double h2 = 0;
	{
		double a = interpolate(genPoint3D(floor(x),floor(y),ceil(z)), width, genPoint3D(ceil(x),floor(y),ceil(z)), x%width);
		//System.out.println(h1);
		double b = interpolate(genPoint3D(floor(x),ceil(y),ceil(z)), width, genPoint3D(ceil(x),ceil(y),ceil(z)), x%width);
		h2 = (int)Math.floor(interpolate(a, width, b, y%width));
	}
	return (int)Math.floor(interpolate(h1, width, h2, z%width));
}
*/