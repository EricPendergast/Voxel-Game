package framework.rendering;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL20;
public class Shader{
	int shaderProgram;
	int vertShader;
	int fragShader;
	public Shader(StringBuilder vertSource, StringBuilder fragSource){
		shaderProgram = glCreateProgram();
		vertShader = glCreateShader(GL_VERTEX_SHADER);
		fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		glShaderSource(vertShader, vertSource);
		glShaderSource(fragShader, fragSource);
		
		glCompileShader(vertShader);
		if(glGetShader(vertShader, GL_COMPILE_STATUS) == GL_FALSE){
			System.err.println("Vertex shader failed to compile.");
			System.exit(0);
		}
		glCompileShader(fragShader);
		if(glGetShader(fragShader, GL_COMPILE_STATUS) == GL_FALSE){
			System.err.println("Fragment shader failed to compile.");
			
			System.exit(0);
		}
		
		glAttachShader(shaderProgram, vertShader);
		glAttachShader(shaderProgram, fragShader);
		
		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
	}
	public void setUniform(String name, float value){
		int loc1 = GL20.glGetUniformLocation(shaderProgram, name);
		GL20.glUniform1f(loc1, value);
	}
	public int getUniformLocation(String name){
		return GL20.glGetUniformLocation(shaderProgram, name);
	}
	public void enable(){
		glUseProgram(shaderProgram);
	}
	public void disable(){
		glUseProgram(0);
	}
	public int getShaderHandle(){
		return shaderProgram;
	}
	public void delete(){
		glDeleteProgram(shaderProgram);
		glDeleteShader(fragShader);
		glDeleteShader(vertShader);
	}
	
	public static StringBuilder loadFile(String path){
		StringBuilder ret = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			while((line = reader.readLine()) != null){
				ret.append(line).append("\n");
			}
		}catch(IOException e){
			System.err.println("Failed to load file: " + path);
		}
		return ret;
	}
}