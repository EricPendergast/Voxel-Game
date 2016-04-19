varying vec3 vertex;
uniform float val;
uniform vec3 playerPos;
uniform vec3 planetPos;
uniform float planetRadius;

float getDistance(vec3 p1, vec3 p2, vec3 v3);

float getWavelength(float hue);
float getHue(vec4 color);
vec4 reyleighScatter(vec4 color);
float distanceThroughAtmosphere(vec3 p1, vec3 v2, vec3 s3, float r);
vec4 blend(vec4 a, vec4 b);
vec4 makeCrosshair(vec2 pos, vec4 color);

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
	atmosphere = blend(aTop,aBot);
	sun.a = fragToSunDist < 0 ? 0 : sun.a;
	sun.a /= max(fragToSunDist-sunRadius,0)*1;
	sun.a = min(1,max(0,sun.a-.5));
	
	
	gl_FragColor = blend(sun,atmosphere);
	
	gl_FragColor = makeCrosshair(vec2(gl_FragCoord), gl_FragColor);
	//gl_FragColor = atmosphere;
	//if(fragToSunDist < sunRadius && fragToSunDist > 0)
	//	gl_FragColor = sun;
	//else
	//	gl_FragColor = vec4(0,0,0,1);
}
/*
void main(){
	float radius = .1;
	vec3 pos = normalize(vertex);
	
	vec3 p1 = vec3(val,val,val); //position of sun/thing
	vec3 p2 = vec3(0,0,0); //position of player
	vec3 v3 = pos; //the direction of the ray from the player to the fragment
	float distance = getDistance(p1,p2,v3);
	
	vec4 sun = vec4(254/255.0,244/255.0,82/255.0,1);
	sun.a = distance<0 ? 0 : sun.a;
	distance -= radius;
	sun = sun*(1-distance);
	
	sun.a = max(sun.a,0);
	sun.a = min(sun.a,1);
	
	//float planetDist = getDistance(planetPos, playerPos, pos);
	vec4 atmTop = vec4(92/255.0,152/255.0,215/255.0,1);
	vec4 atmBottom = vec4(228/255.0, 241/255.0, 247/255.0,1);
	vec4 atmosphere = vec4(92/255.0,152/255.0,215/255.0,1);
	float atmosDist = distanceThroughAtmosphere(playerPos, pos, planetPos, planetRadius);
	//atmosphere *= atmosDist/10;
	atmTop.a *= atmosDist/10;
	atmosphere = blend(atmTop,atmBottom);
	gl_FragColor = blend(sun, atmosphere);//vec4(sun.a*(sun.r) + (1-sun.a)*atmosphere.r, sun.a*(sun.g) + (1-sun.a)*atmosphere.g, sun.a*(sun.b) + (1-sun.a)*atmosphere.b, 1);
	//if(sun.a > 0)
	//	gl_FragColor.r = 1;
	//gl_FragColor = vec4(sun.a*(sun.r) + (1-sun.a)*atmosphere.r, sun.a*(sun.g) + (1-sun.a)*atmosphere.g, sun.a*(sun.b) + (1-sun.a)*atmosphere.b, 1);//
	//gl_FragColor = sun + atmosphere*(1-sun.a);
	//if(atmosDist > 0)
	//	gl_FragColor = atmosphere; //vec4(0,1,0,1000)/(atmosDist/10);
	//else
	//	gl_FragColor = sun;
	//gl_FragColor = blue*atmosDist;//reyleighScatter(color);//vec4(getDistance(p1,p2,v3),0,0,1);
}
*/
vec4 blend(vec4 a, vec4 b){
	//return vec4(sqrt(pow(a.a*(a.r),2) + pow((1-a.a)*b.r,2)), sqrt(pow(a.a*(a.g),2) + pow((1-a.a)*b.g,2)), sqrt(pow(a.a*(a.b),2) + pow((1-a.a)*b.b,2)), (a.a+b.a)/2);
	//return vec4(a.a*(a.r) + (1-a.a)*b.r, a.a*(a.g) + (1-a.a)*b.g, a.a*(a.b) + (1-a.a)*b.b, (a.a+b.a)/2);
	//return vec4(min(a.r+b.r,1),min(a.g+b.g,1),min(a.b+b.b,1),1);
	vec4 ret = vec4(0,0,0, 1 - (1-a.a)*(1-b.a));
	
	ret.r = a.r*a.a/ret.a + b.r*b.a*(1-a.a)/ret.a;
	ret.g = a.g*a.a/ret.a + b.g*b.a*(1-a.a)/ret.a;
	ret.b = a.b*a.a/ret.a + b.b*b.a*(1-a.a)/ret.a;
	return ret;
	
}
vec4 reyleighScatter(vec4 color){
	//float wavelength = getWavelength(getHue(color));
	
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
float getWavelength(float hue){
	return 400 + 360 - hue;
}
//returns approximate hue value for the rgb value, ranging from 0-360
float getHue(vec4 color){
	int max = 0;
	int min = 0;
	for(int i = 0; i < 3; i++){
		max = color[i]>color[max] ? i : max;
		min = color[i]<color[min] ? i : min;
	}
	
	return mod(60*(2.0*max + (color[int(mod(max+1,3))]-color[int(mod(max+2,3))])/(color[max]-color[min]))+360,360);
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