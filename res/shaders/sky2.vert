varying vec3 vertex;
uniform float color;

void main(){
	
	gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
	vertex = gl_Vertex.xyz;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}