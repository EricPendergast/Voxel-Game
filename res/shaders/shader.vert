varying vec4 vertColor;
varying vec3 vertex;
void main(){
	vec4 thing = gl_Vertex;
	//int a = thing.x;
	//thing.x = thing.x;
	//int a = gl_Vertex.w;
	gl_Position = gl_ModelViewProjectionMatrix*thing;
	
	vertex = gl_Vertex.xyz;
	//gl_Color = vec4(
	vertColor = gl_Color;//vec4(1.3, 1.3, 1.2, 1)*gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}