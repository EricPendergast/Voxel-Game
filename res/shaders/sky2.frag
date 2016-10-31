varying vec3 vertex;
uniform float val;
uniform vec3 playerPos;
uniform vec3 planetPos;
uniform float planetRadius;

float getDistance(vec3 p1, vec3 p2, vec3 v3);

//float getWavelength(float hue);
//float getHue(vec4 color);
//vec4 reyleighScatter(vec4 color);
float distanceThroughAtmosphere(vec3 p1, vec3 v2, vec3 s3, float r);
//vec4 blend(vec4 a, vec4 b);
vec4 makeCrosshair(vec2 pos, vec4 color);
vec4 toARGB(vec4 color);

/*
void main(){
	float sunRadius = .1;
	vec3 fragPos = normalize(vertex);
	
	vec3 sunPos = vec3(val,val,val); //position of sun/thing
	vec3 sunPOV = vec3(0,0,0); //position of player
	float fragToSunDist = getDistance(sunPos, sunPOV, fragPos);
	//fragToSunDist = fragToSunDist<0 ? sunRadius+1 : fragToSunDist;
	
	vec4 sun = vec4(254/255.0,244/255.0,82/255.0,1);
	float atmosDist = distanceThroughAtmosphere(playerPos, fragPos, planetPos, planetRadius);
	vec4 atmosphere = vec4(92/255.0,152/255.0,215/255.0,1);
	vec4 aTop = vec4(92/255.0,152/255.0,215/255.0,1);
	vec4 aBot = vec4(1,1,1,1);
	atmosDist /= 50;
	atmosDist = min(1, atmosDist);
	atmosDist = max(0, atmosDist);
	aTop.a = atmosDist;
	aBot.a = 1-atmosDist;
	
	//atmosphere.a = atmosDist/50;
	//atmosphere = blend(aTop,aBot);
	sun.a = fragToSunDist < 0 ? 0 : sun.a;
	sun.a /= max(fragToSunDist-sunRadius,0)*1;
	sun.a = min(1,max(0,sun.a-.5));
	
	
	//gl_FragColor = blend(sun,atmosphere);
	
	gl_FragColor = makeCrosshair(vec2(gl_FragCoord), gl_FragColor);
	//gl_FragColor = atmosphere;
	//if(fragToSunDist < sunRadius && fragToSunDist > 0)
	//	gl_FragColor = sun;
	//else
	//	gl_FragColor = vec4(0,0,0,1);
}
*/

void main(){
	float sunRadius = .1;
	vec3 fragPos = normalize(vertex);
	
	vec3 sunPos = vec3(val,val,val); //position of sun/thing
	vec3 sunPOV = vec3(0,0,0); //position of player
	
	float fragToSunDist = getDistance(sunPos, sunPOV, fragPos);
	
	if(fragToSunDist < sunRadius)
		gl_FragColor = toARGB(vec4(12,10,.5,1));
	else
		gl_FragColor = vec4(0,0,0,1);
		
	
}


vec4 toARGB(vec4 color){
	color.r = -1.0/(abs(max(0,color.r))+1)+1;
	color.g = -1.0/(abs(max(0,color.g))+1)+1;
	color.b = -1.0/(abs(max(0,color.b))+1)+1;
	color.a = 1;
	
	return color;
}
//p1 is position of player
//v2 is direction of the fragment
//s3 is the position of the sphere
//r is the radius of the sphere
//returns the positive value of t that makes p1+v2*t lie on the surfce of the sphere
float distanceThroughAtmosphere(vec3 p1, vec3 v2, vec3 s3, float r){
	float a = v2.x*v2.x  +  v2.y*v2.y  +  v2.z*v2.z;
	float b = 2*p1.x*v2.x - 2*v2.x*s3.x  +  2*p1.y*v2.y - 2*v2.y*s3.y  +  2*p1.z*v2.z - 2*v2.z*s3.z ;
	float c = p1.x*p1.x - 2*p1.x*s3.x + s3.x*s3.x  +  p1.y*p1.y - 2*p1.y*s3.y + s3.y*s3.y  +  p1.z*p1.z - 2*p1.z*s3.z + s3.z*s3.z - r*r;
	
	float d = sqrt(b*b - 4*a*c);
	
	float sol1 = (-b+d)/(2*a);
	float sol2 = (-b-d)/(2*a);
	if(min(sol1,sol2) > 0)
		return abs(sol1-sol2);
	else
		return max(sol1,sol2);
}
float getDistance(vec3 p1, vec3 p2, vec3 v3){
	//distance between p1 and (p2 + v3*t) is at a minimum
	float t = ((p1.x-p2.x)*v3.x + (p1.y-p2.y)*v3.y + (p1.z-p2.z)*v3.z)/(dot(v3,v3));
	
	float dist = sqrt(pow(p2.x+v3.x*t-p1.x,2) + pow(p2.y+v3.y*t-p1.y,2) + pow(p2.z+v3.z*t-p1.z,2));
	
	return dist*(t > 0 ? 1 : -1);
}


uniform vec2 crosshairPos;
uniform float crosshairWidth;
vec4 makeCrosshair(vec2 pos, vec4 color){
	pos.x = abs(pos.x-crosshairPos.x);
	pos.y = abs(pos.y-crosshairPos.y);
	if( (pos.x-crosshairWidth < 0 && pos.y-crosshairWidth*7 < 0) || (pos.y-crosshairWidth < 0 && pos.x-crosshairWidth*7 < 0))
		return vec4(1-color.r,1-color.g,1-color.b,1);
		//return vec4(mod((color.r+.3),1),mod((color.g+.3),1),mod((color.b+.3),1),1);
	else
		return color;
}