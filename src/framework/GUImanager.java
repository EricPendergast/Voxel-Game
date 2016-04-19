package framework;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15.*;
import org.lwjgl.opengl.GL11.*;
public class GUImanager {
	private DisplayMode displayMode;
	private GUI defaultGUI;
	public GUImanager(DisplayMode dm){
		 displayMode = dm;
		 defaultGUI = new GUI("res/gui.png", displayMode);
	}
	public void render(){
//		GL11.glBegin(GL11.GL_QUADS);
//		
//		GL11.glVertex2f( 0f, 0f);
//		GL11.glVertex2f( 1f, 0f);
//		GL11.glVertex2f( 1f, 1f);
//		GL11.glVertex2f( 0f, 1f);
//		
//		GL11.glTexCoord2f(0, 0);
//		GL11.glEnd();
		
		defaultGUI.render();
	}
	public void update(){
		
	}
}
