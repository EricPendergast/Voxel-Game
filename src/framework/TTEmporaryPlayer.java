package framework;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class TTEmporaryPlayer {
	float x = 0;
	float y = 0;
	float z = 0;
	float yRot = 0;
	float xzRot = 0;
	float tracking = .1f;
	float speed = .2f;
	
	public TTEmporaryPlayer(){
		//Mouse.setNativeCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "cursor"));
		Mouse.setGrabbed(true);
		Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
	}
	public void update(){
		if(Display.isActive()){
			yRot += (Mouse.getX() - Display.getWidth()/2)*tracking;
			yRot %= 360;
			xzRot += (Mouse.getY() - Display.getHeight()/2)*tracking;
			if(xzRot > 90)
				xzRot = 90;
			else if(xzRot < -90)
				xzRot = -90;
			Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
		}
//		Matrix4f mat = new Matrix4f();
//		mat.rotate(yRot, new Vector3f(0,1,0));
//		Vector3f dir = new Vector3f(0,0,-speed);
//		//mat.translate(dir);
//		System.out.println(mat);
		Vector3f nv = Ops.rotateVector3f(yRot, new Vector3f(0,0,speed), new Vector3f(0,1,0));
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			x -= nv.x;
			z -= nv.z;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			x += nv.x;
			z += nv.z;
		}
		Vector3f nv2 = Ops.rotateVector3f(-90, nv, new Vector3f(0,1,0));
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			x -= nv2.x;
			z -= nv2.z;
		}if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			x += nv2.x;
			z += nv2.z;
		}
	}
	
	public void render(){
		
		GL11.glRotatef(-xzRot, 1, 0, 0);
		GL11.glRotatef(yRot, 0, 1, 0);
		GL11.glTranslatef(-x,-y,-z);
//		xzRot = 0;
//		yRot = 0;
//		x = y = z = 0;
	}
}
