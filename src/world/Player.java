package world;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import world.blocks.Block;
import framework.Blocks;
import framework.Ops;
import framework.OpsMut;
import framework.Runner;
import framework.Timer;
import framework.rendering.VBOSource;
public class Player implements Entity{
	public PhysicsBody body;
	public static final int x = 0;
	public static final int y = 1;
	public static final int z = 2;
	
	float speed = 2f;//4;
	float accel = .05f;//.03f;//1;
	float gravity = .025f;//.018f;//.7f;
	float jumpHeight = .26f;//.21f;
	float pitch;
	float reach = 500f;
	float tracking = .1f;
	float friction = .65f;
	public boolean collided = false;
	
	float[] blockLook = null;
	float[] blockPlace = null;
	
	Sky sky;
	/**
	 * 
	 */
	public Player(){
		Mouse.setGrabbed(true);
		Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
		body = new PhysicsBody();
		body.pos[y] = 120;
		//body.pos[x] = 6;
		//body.pos[z] = -6;
		body.dir[z] = .0f;
		body.hitboxes = new float[2][];
		//lower
		body.hitboxes[0] = new float[]{0, -1f - .2f, 0, .35f};
		//mid
		//body.hitboxes[1] = new float[]{0, -1f - .2f, 0, .35f}; //new float[]{0,0 - .2f,0,.35f};
		//upper
		body.hitboxes[1] = new float[]{0, .05f - .2f, 0, .35f};
		
		body.hitboxCollided = new boolean[2];
		body.lowestCollideAng = (float)Math.toRadians(30);
		
		sky = new Sky();
	}
	/**
	 * 
	 * @param closestPlanet
	 */
	int jumpTimer = 0;
	public void update(Planet closestPlanet){
		Timer debug = new Timer();
		debug.start();
		String debugStr = "";
		debugStr += "1:" + debug.getTime() + " ";
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			OpsMut.add(body.dir, Ops.multiply(body.front,.1f));
		}
		
		jumpTimer++;
		body.gravity = OpsMut.multiply(OpsMut.normalize(closestPlanet.getGravityVector(/*body.pos[x],body.pos[y],body.pos[z]*/body.pos)),gravity);
		//update rotation of the camera and position vectors to line up with the gravity
		body.updateGravRotation();
		
		float tempAccel = accel;
		debugStr += "2:" + debug.getTime() + " ";
		if(body.hitboxCollided[0]){//if the lowest hitbox is on the ground
			//friction of the ground
			OpsMut.multiply(body.movement, friction);
			//this is so you can walk up sloped surfaces without gravity pulling you down
			OpsMut.multiply(body.dir, 0f);
			//this is here because jumping can only occur if there is a collision
			updateJump();
		}
		else{
			//accel is temporarily changed so that it is harder to steer yourself in mid air
			accel = accel/8;
			
		}
		debugStr += "3:" + debug.getTime() + " ";
		updateMovement();
		accel = tempAccel;
		
		body.update();
		debugStr += "4:" + debug.getTime() + " ";
		//body.updateCollisions(closestPlanet);
		body.updateCollisionsAdvanced(closestPlanet);
		//body.updateCollisionsBasic(closestPlanet);
		
		updateBlockLook(closestPlanet);
		debugStr += "5:" + debug.getTime() + " ";
		updateBlockPlacement(closestPlanet);
		debugStr += "6:" + debug.getTime() + " ";
		
		if(debug.getTime() > 2)
			System.out.println("Player update() " + debugStr);
	}
	
	/**
	 * makes user jump away from direction of gravity if space is pressed
	 */
	
	private void updateJump(){
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && jumpTimer > 10){
			//OpsMut.add(body.dir, Ops.multiply(body.up, .1f));//.22f));//.175f));//.155f));
			body.jump(jumpHeight);
			//jumpTimer = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C) && jumpTimer > 10){
			//OpsMut.add(body.dir, Ops.multiply(body.up, .1f));//.22f));//.175f));//.155f));
			body.jump(1);
			//jumpTimer = 0;
		}
	}
	/**
	 * changes movement vectors to reflect user input
	 */
	private void updateMovement(){
		if(Display.isActive()){
			pitch += (Mouse.getY() - Display.getHeight()/2)*tracking;
			if(pitch > 89)
				pitch = 89;
			else if(pitch < -89)
				pitch = -89;
			
			//rotate for yaw
			body.front = Ops.rotate(body.front, body.up, -(float)Math.toRadians(Mouse.getX() - Display.getWidth()/2)*tracking);
			body.right = Ops.rotate(body.right, body.up, -(float)Math.toRadians(Mouse.getX() - Display.getWidth()/2)*tracking);
			
			Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
		}
		OpsMut.multiply(body.movement, .98f);
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			OpsMut.add(body.movement, Ops.multiply(body.front,accel));
		}if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			OpsMut.subtract(body.movement, Ops.multiply(body.front,accel));
		}if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			OpsMut.subtract(body.movement, Ops.multiply(body.right,accel));
		}if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			OpsMut.add(body.movement, Ops.multiply(body.right,accel));
		}
		
		if(Ops.getLengthsq(body.movement) > speed*speed){
			OpsMut.multiply(OpsMut.normalize(body.movement),speed);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			body.pos = new float[]{0,230,0};
			body.dir = new float[3];
		}
	}
	/**
	 * Does all the rendering stuff
	 * Translates camera, draws selection cube
	 */
	public void render(Planet planet){
		float[][] look = getDirLook();
		spinInDirection(look[0],look[1],look[2]);
//		sky.render(planet, body.pos);
		
		//float[] newPos = {body.pos[x],body.pos[y],body.pos[z]};
		//OpsMut.subtract(newPos, Ops.multiply(Ops.normalize(body.gravity),.3f));
		//GL11.glTranslatef(-newPos[x],-newPos[y],-newPos[z]);
		GL11.glTranslatef(-body.pos[x],-body.pos[y],-body.pos[z]);
//		if(blockLook != null){
//			Blocks.renderBlock((byte)49, (int)blockLook[x], (int)blockLook[y], (int)blockLook[z]);
//		}
		if(blockLook != null)
			Blocks.drawWireCube(blockLook);
		
	}
	
	//front,right,up
	/**
	 * Returns an array of 'body.front', 'right', and 'up', adjusted 
	 * so that 'front' points in the direction the player looks and the three vectors are still orthagonal
	 * @return
	 */
	public float[][] getDirLook(){
		return new float[][]{	Ops.rotate(body.front, body.right, (float)Math.toRadians(pitch)),
								new float[]{body.right[x],body.right[y],body.right[z]},
								Ops.rotate(body.up, body.right, (float)Math.toRadians(pitch))};
	}
	
	/**
	 * rotates the opengl camera to point in 'forward' direction, and also be spun in the correct direction
	 * @param forward
	 * @param right
	 * @param up
	 */
	private void spinInDirection(float[] forward, float[] right, float[] up){
		float[] forwardO = {0,0,-1};
		float[] rightO = {1,0,0};
		float[] upO = {0,1,0};
		
		
		float rotA, rotB, rotC;
		
		float a = Ops.getAngleBetween(new float[]{forward[x],0,forward[z]},forwardO);
		if(forward[x] < 0)
			a = -a;
		forward = Ops.rotate(forward, upO, a);
		right = Ops.rotate(right, upO, a);
		up = Ops.rotate(up, upO, a);
		rotA = (float)Math.toDegrees(a);
		
		a = Ops.getAngleBetween(forward,forwardO);
		if(forward[y] > 0)
			a = -a;
		forward = Ops.rotate(forward, rightO, a);
		right = Ops.rotate(right, rightO, a);
		up = Ops.rotate(up, rightO, a);
		rotB = (float)Math.toDegrees(a);
		
		a = Ops.getAngleBetween(right, rightO);
		if(right[y] < 0)
			a = -a;
		right = Ops.rotate(right, forwardO, a);
		up = Ops.rotate(up, forwardO, a);
		rotC = (float)Math.toDegrees(a);
		
		
		GL11.glRotatef(rotC, 0, 0, -1);
		GL11.glRotatef(rotB, 1,0,0);
		GL11.glRotatef(rotA, 0,1,0);
	}
	/**
	 * changes 'blockLook' to be the position of the block the user is looking at,
	 * null if too far away or if the user isn't looking at a block
	 * @param p
	 */
	private void updateBlockLook(Planet p){
		float[] dirLook = getDirLook()[0];
		float[] pos = Ops.copy(body.pos);
		int minIndex = 0;
		blockLook = null;
		blockPlace = null;
		for(int i = 0; i < 1000; i++){
			float[] allScales = new float[6];
			//allScales[i] is the number that dirLook has to be multiplied by so that dirLook + pos intersects with the boundary between two voxels
			allScales[0] = (Math.round(pos[x])+.5f-pos[x])/dirLook[x];
			allScales[1] = (Math.round(pos[x])-.5f-pos[x])/dirLook[x];
			
			allScales[2] = (Math.round(pos[y])+.5f-pos[y])/dirLook[y];
			allScales[3] = (Math.round(pos[y])-.5f-pos[y])/dirLook[y];
			
			allScales[4] = (Math.round(pos[z])+.5f-pos[z])/dirLook[z];
			allScales[5] = (Math.round(pos[z])-.5f-pos[z])/dirLook[z];
			for(int j = 0; j < allScales.length; j++){
				allScales[j] = allScales[j] <= 0 ? 2:allScales[j];
			}
			minIndex = 0;
			for(int j = 1; j < 6; j++){
				if((allScales[j] < allScales[minIndex]))
					minIndex = j;
			}
			if(allScales[minIndex] == 2)
				break;
			OpsMut.add(pos, Ops.multiply(dirLook, allScales[minIndex]+.0001f));
			Block blockA = p.getBlock(Math.round(pos[x]-(minIndex/2==x?.5f:0)), Math.round(pos[y]-(minIndex/2==y?.5f:0)), Math.round(pos[z]-(minIndex/2==z?.5f:0)));
			Block blockB = p.getBlock(Math.round(pos[x]+(minIndex/2==x?.5f:0)), Math.round(pos[y]+(minIndex/2==y?.5f:0)), Math.round(pos[z]+(minIndex/2==z?.5f:0)));
			
			if(blockA.isSolid() != blockB.isSolid()){
				blockLook = new float[3];
				blockLook[x] = Math.round(pos[x]+(minIndex/2==0?(blockA.isSolid()?-.5f:.5f):0));
				blockLook[y] = Math.round(pos[y]+(minIndex/2==1?(blockA.isSolid()?-.5f:.5f):0));
				blockLook[z] = Math.round(pos[z]+(minIndex/2==2?(blockA.isSolid()?-.5f:.5f):0));
				blockPlace = new float[3];
				blockPlace[x] = Math.round(pos[x]+(minIndex/2==0?(!blockA.isSolid()?-.5f:.5f):0));
				blockPlace[y] = Math.round(pos[y]+(minIndex/2==1?(!blockA.isSolid()?-.5f:.5f):0));
				blockPlace[z] = Math.round(pos[z]+(minIndex/2==2?(!blockA.isSolid()?-.5f:.5f):0));
				break;
			}
		}
		if(Ops.distanceBetween(body.pos, pos) > reach){
			blockLook = null;
			blockPlace = null;
		}
	}
	
	public void interact(Entity e) {
		
	}
	/**
	 * returns the position of the player
	 * @return
	 */
	public float[] getPos(){
		return body.pos;
	}
	/**
	 * returns the velocity of the player
	 * @return
	 */
	public float[] getDir(){
		return body.dir;
	}
	/**
	 * Takes mouse input and 
	 */
	boolean leftInit = false;
	boolean rightInit = false;
	
	int leftTimer = 0;
	int rightTimer = 0;
	private void updateBlockPlacement(Planet p){
		rightTimer++;
		leftTimer++;
		int timeLim = 10;
		//if(!leftInit & (leftInit = Mouse.isButtonDown(0)) && blockLook != null){
		if(leftTimer > timeLim && blockLook != null &&  Mouse.isButtonDown(0)){
			leftTimer = 0;
			p.setBlock((int)blockLook[x], (int)blockLook[y], (int)blockLook[z], (byte)0, VBOSource.fromPlayer);
		}
		//if(!rightInit & (rightInit = Mouse.isButtonDown(1)) && blockPlace != null){
		if(rightTimer > timeLim && blockPlace != null && Mouse.isButtonDown(1)){
			rightTimer = 0;
			p.setBlock((int)blockPlace[x], (int)blockPlace[y], (int)blockPlace[z], Blocks.STONE, VBOSource.fromPlayer);
		}
		if(!Mouse.isButtonDown(1))
			rightTimer = 1000000;
		if(!Mouse.isButtonDown(0))
			leftTimer = 1000000;
	}
}