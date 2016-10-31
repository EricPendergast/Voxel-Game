package world;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL20;

import framework.Globals;
import framework.Ops;
import framework.OpsMut;
import framework.rendering.Shader;

public class Sky {
	Shader shader;
	Planet planet;
	public Sky(){
		class SShader extends Shader{
			public SShader(StringBuilder vertSource, StringBuilder fragSource) {super(vertSource, fragSource);}
			public void enable(){
				super.enable();
				setUniform("crosshairWidth", Globals.crosshairWidth);
				GL20.glUniform2f(getUniformLocation("crosshairPos"), Globals.crosshairPos[0], Globals.crosshairPos[1]);
			}
		}
		shader = new SShader(Shader.loadFile("res/shaders/sky.vert"),Shader.loadFile("res/shaders/sky.frag"));
	}
	float counter = 0;
	public void render(Planet planet, float[] playerPos){
		counter += .1;
		shader.enable();
		//distance of the sun
		shader.setUniform("val", counter);
		GL20.glUniform3f(shader.getUniformLocation("playerPos"), playerPos[0], playerPos[1], playerPos[2]);
		//int maxIndex = 0;
		//for(int i = 1; i < 3; i++){
		//	maxIndex = playerPos[maxIndex] < playerPos[i] ? i : maxIndex;
		//}
		////float max = Math.max(playerPos[0], Math.max(playerPos[1],playerPos[2]))/230;
		//float boxW = 100;
		//GL20.glUniform3f(shader.getUniformLocation("planetPos"), playerPos[0]/playerPos[maxIndex]*boxW, playerPos[1]/playerPos[maxIndex]*boxW, playerPos[2]/playerPos[maxIndex]*boxW);
		float[] atmPos = getAtmospherePosition(playerPos, planet);
		GL20.glUniform3f(shader.getUniformLocation("planetPos"), atmPos[0], atmPos[1], atmPos[2]);
		shader.setUniform("planetRadius", 25);
		
		glDisable(GL_DEPTH_TEST);
		glColor4f(1,1,1,1);
	    glBegin(GL_QUADS);glVertex3f(  0.5f, -0.5f, -0.5f );glVertex3f( -0.5f, -0.5f, -0.5f );glVertex3f( -0.5f,  0.5f, -0.5f );glVertex3f(  0.5f,  0.5f, -0.5f );glEnd();
	    glBegin(GL_QUADS);glVertex3f(  0.5f, -0.5f,  0.5f );glVertex3f(  0.5f, -0.5f, -0.5f );glVertex3f(  0.5f,  0.5f, -0.5f );glVertex3f(  0.5f,  0.5f,  0.5f );glEnd();
	    glBegin(GL_QUADS);glVertex3f( -0.5f, -0.5f,  0.5f );glVertex3f(  0.5f, -0.5f,  0.5f );glVertex3f(  0.5f,  0.5f,  0.5f );glVertex3f( -0.5f,  0.5f,  0.5f );glEnd();
	    glBegin(GL_QUADS);glVertex3f( -0.5f, -0.5f, -0.5f );glVertex3f( -0.5f, -0.5f,  0.5f );glVertex3f( -0.5f,  0.5f,  0.5f );glVertex3f( -0.5f,  0.5f, -0.5f );glEnd();
	    glBegin(GL_QUADS);glVertex3f( -0.5f,  0.5f, -0.5f );glVertex3f( -0.5f,  0.5f,  0.5f );glVertex3f(  0.5f,  0.5f,  0.5f );glVertex3f(  0.5f,  0.5f, -0.5f );glEnd();
	    glBegin(GL_QUADS);glVertex3f(  0.5f, -0.5f, -0.5f );glVertex3f(  0.5f, -0.5f,  0.5f );glVertex3f( -0.5f, -0.5f,  0.5f );glVertex3f( -0.5f, -0.5f, -0.5f );glEnd();
		glEnable(GL_DEPTH_TEST);
		shader.disable();
	}
	
	public float[] getAtmospherePosition(float[] pos, Planet planet){
		float[] gravCenter = Ops.copy(pos);
		int maxIndex = 0;
		for(int i = 1; i < 3; i++)
			maxIndex = Math.abs(pos[maxIndex]) < Math.abs(pos[i]) ? i : maxIndex;
		for(int i = 0; i < 3; i++){
			if(i == maxIndex || Math.abs(gravCenter[i]) > planet.radius){
				gravCenter[i] = Math.signum(gravCenter[i])*planet.radius;
			}//else{
			//	if(Math.abs(gravCenter[i]) > planet.radius){
			//		gravCenter[i] = Math.signum(gravCenter[i])*planet.radius;
			//	}
			//}
		}
		
		return gravCenter;
	}
}

/*
public float[] getSquareGravityVector(float[] pos){
		pos = Ops.normalize(pos);
		OpsMut.multiply(pos, this.radius*2);
		float[] gravCenter = Ops.copy(pos);
		int maxIndex = 0;
		for(int i = 1; i < 3; i++)
			maxIndex = Math.abs(pos[maxIndex]) < Math.abs(pos[i]) ? i : maxIndex;
		for(int i = 0; i < 3; i++){
			if(i == maxIndex){
				gravCenter[i] = Math.signum(gravCenter[i])*this.radius;
			}else{
				if(Math.abs(gravCenter[i]) > this.radius){
					gravCenter[i] = Math.signum(gravCenter[i])*this.radius;
				}
			}
		}
		
		OpsMut.multiply(OpsMut.subtract(pos, gravCenter),-1);
		pos[0] += .000001;
		pos[1] += .000001;
		pos[2] += .000001;
		return pos;
	}

*/