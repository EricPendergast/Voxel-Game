varying vec4 vertColor;
varying vec3 vertex;
uniform sampler2D texture1;
uniform float i;

vec4 blend(vec4 a, vec4 b);
vec4 makeCrosshair(vec2 pos, vec4 color);
vec4 toARGB(vec4 color);

void main(){
	
	gl_FragColor = (texture2D(texture1, gl_TexCoord[0].st)*vertColor);
	
	
	gl_FragColor = makeCrosshair(vec2(gl_FragCoord), gl_FragColor);
	
	// = vec4(0,0,0,1);
	
}

/*
void main(){
	//4,4,4
	//.5,1,3
	gl_FragColor = vec4(3,3,3,0) + vec4(.5*i,1*i,3*i,0);// + vec4(i,0,0,0);
	//gl_FragColor = gl_FragColor/(pow(2.718,gl_FragColor.a));
	gl_FragColor = toARGB(gl_FragColor);
	//gl_FragColor = toARGB(vec4(2,.1,.1, 100));
}
*/
vec4 toARGB(vec4 color){
	color.r = -1.0/(abs(max(0,color.r))+1)+1;
	color.g = -1.0/(abs(max(0,color.g))+1)+1;
	color.b = -1.0/(abs(max(0,color.b))+1)+1;
	color.a = 1;
	
	return color;
}
vec4 blend(vec4 a, vec4 b){
	vec4 ret = vec4(0,0,0,0);
	
	ret.r = (a.r + b.r); 
	ret.g = (a.g + b.g);
	ret.b = (a.b + b.b);
	
	return ret;
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

//gl_FragColor = vertColor;
//if(isCrosshair(vec2(gl_FragCoord))){   //gl_FragCoord.y > 400 && gl_FragCoord.y < 410){
	//	gl_FragColor = vec4(1-gl_FragColor.r,1-gl_FragColor.g,1-gl_FragColor.b,1);
	//}
	//gl_FragColor.r = 0;
	
	
//gl_FragColor = vec4(0,0,0,1);
	//if(temp.r > 0 || temp.r == 0)
	
	
	//float i = gl_FragCoord.x;
	
	//gl_FragColor = (vec4(0,0,gl_FragCoord.w*10, 1) + vertColor)/2;
	//if(gl_FragCoord.w > .1){
	//	gl_FragColor = vec4(1,0,0,1);
	//}
	
/*
vec4 blend(vec4 a, vec4 b){
	//return vec4(sqrt(pow(a.a*(a.r),2) + pow((1-a.a)*b.r,2)), sqrt(pow(a.a*(a.g),2) + pow((1-a.a)*b.g,2)), sqrt(pow(a.a*(a.b),2) + pow((1-a.a)*b.b,2)), (a.a+b.a)/2);
	//return vec4(a.a*(a.r) + (1-a.a)*b.r, a.a*(a.g) + (1-a.a)*b.g, a.a*(a.b) + (1-a.a)*b.b, (a.a+b.a)/2);
	//return vec4(min(a.r+b.r,1),min(a.g+b.g,1),min(a.b+b.b,1),1);
	//vec4 ret = vec4(0,0,0, 1 - (1-a.a)*(1-b.a));
	vec4 ret = vec4(0,0,0, 0);
	
	//ret.r = a.r*a.a/ret.a + b.r*b.a*(1-a.a)/ret.a;
	//ret.g = a.g*a.a/ret.a + b.g*b.a*(1-a.a)/ret.a;
	//ret.b = a.b*a.a/ret.a + b.b*b.a*(1-a.a)/ret.a;
	
	ret.r = (a.r + b.r); 
	ret.g = (a.g + b.g);
	ret.b = (a.b + b.b);
	
	//float max = max(max(abs(ret.r),abs(ret.g)),abs(ret.b));
	
	//ret.r /= ret.a;
	//ret.g /= ret.a;
	//ret.b /= ret.a;
	//ret.r = a.r*ret.a/ret.a + b.r*b.a*(1-ret.a)/ret.a;
	//ret.g = a.g*ret.a/ret.a + b.g*b.a*(1-ret.a)/ret.a;
	//ret.b = a.b*ret.a/ret.a + b.b*b.a*(1-ret.a)/ret.a;
	return ret;
	
}

vec4 toARGB(vec4 color){
	//float m = -1.0/(abs(color.a)+1)+1;
	//color.a = color.r + color.g + color.b;
	//color = (color) * color.a;
	//color.r += (-1.0/(abs(color.r)+1)+1)*(color.a-color.r);
	//color.g += (-1.0/(abs(color.g)+1)+1)*(color.a-color.g);
	//color.b += (-1.0/(abs(color.b)+1)+1)*(color.a-color.b);
	
	color.r = -1.0/(abs(color.r)+1)+1;
	color.g = -1.0/(abs(color.g)+1)+1;
	color.b = -1.0/(abs(color.b)+1)+1;
	color.a = 1;
	return color;
}
*/