package world;

import framework.Blocks;
import framework.Ops;
import framework.OpsMut;
import framework.Timer;

//TODO Make collisions work for more general cases
public class PhysicsBody {
	public static final int x = 0;
	public static final int y = 1;
	public static final int z = 2;
	public float[] pos;
	// Partial velocity of the physics body.
	public float[] dir;
	public float[] gravity;
	//a supplemental direction vector
	public float[] movement;
	
	//a list of the hitboxes
	//hitboxes are cubes in the form {x, y, z, height/2}, where x, y, and z are
	//relative to the position and rotation of the PhysicsBody
	public float[][] hitboxes;
	//each index represents whether the corresponding index in 'hitboxes'
	//collided with a block at an angle greater than 'lowestCollideAng'
	public boolean[] hitboxCollided;
	public float lowestCollideAng;
	
	float[] up = {0,1,0};
	float[] front = {0,0,-1};
	float[] right = {1,0,0};
	
	Timer timer;
	
	public PhysicsBody(){
		pos = new float[3];
		dir = new float[3];
		gravity = new float[3];
		movement = new float[3];
		timer = new Timer();
		timer.start();
	}
	/**
	 * 
	 */
	public void update(){
		float secs = timer.getTime()/1000f;
//		OpsMut.add(dir, Ops.multiply(gravity,secs));
//		OpsMut.add(pos, dir);
//		OpsMut.add(pos, Ops.multiply(movement, secs));
//		OpsMut.add(dir, Ops.multiply(gravity, s*16.666f/timer.getTime()));
//		OpsMut.add(pos, Ops.multiply(dir, s*16.666f/timer.getTime()));
//		OpsMut.add(pos, Ops.multiply(movement, s*16.666f/timer.getTime()));
		OpsMut.add(dir, gravity);
		OpsMut.add(pos, dir);
		OpsMut.add(pos, movement);
		timer.start();
	}
	
	public void jump(float jumpHeight){
		OpsMut.add(dir, Ops.multiply(up, jumpHeight));
	}
	/**
	 * updates the rotation of 'up', 'right', and 'front' so that 'up' lines up with the gravity
	 */
	public void updateGravRotation(){
		float[] g = Ops.multiply(gravity,-1);
		float[] cross = Ops.crossProduct(up, g);
		OpsMut.normalize(cross);
		
		float theta = Ops.getAngleBetween(up, g);
		if(!Float.isFinite(theta))
			return;
		up = Ops.rotate(up, cross, theta);
		front = Ops.rotate(front, cross, theta);
		right = Ops.rotate(right, cross, theta);
	}
	/**
	 * returns the hitbox at the index given
	 * accounts for rotation
	 * @param index
	 * @return
	 */
	public float[] getHitbox(int index){
		float[] ret = new float[4];
		OpsMut.add(ret, Ops.multiply(right, hitboxes[index][x]));
		OpsMut.add(ret, Ops.multiply(up, 	hitboxes[index][y]));
		OpsMut.add(ret, Ops.multiply(front, -hitboxes[index][z]));
		
		ret[3] = hitboxes[index][3];
		
		OpsMut.add(ret, pos);

		return ret;
	}
	/**
	 * 
	 * @return number of hitboxes
	 */
	public int numHitboxes(){
		return hitboxes.length;
	}
	
	/**
	 * Makes the PhysicsBody collide with the blocks in the given planet. Only
	 * collides with blocks surrounding the player at his/her current position.
	 * @param planet
	 */
	public boolean updateCollisions(Planet planet){
		// Whether or not there was a collision
		boolean collision = false;
		
		// Reset all hitbox hit statuses
		for(int i = 0; i < hitboxCollided.length; i++)
			hitboxCollided[i] = false;
		//TODO: Make this work for more general cases, not just for the two hitbox player
		float[] upperBox = getHitbox(1);
		float[] lowerBox = getHitbox(0);
		// The approximate position of the center of the physics body. Right now
		// it is just the position of the upper box
		int[] blockPos = new int[]{Math.round(upperBox[x]), Math.round(upperBox[y]), Math.round(upperBox[z])};
		int[] blockPosLower = new int[]{Math.round(lowerBox[x]),Math.round(lowerBox[y]),Math.round(lowerBox[z])};
		
		int[] diff = new int[]{blockPos[x]-blockPosLower[x],blockPos[y]-blockPosLower[y],blockPos[z]-blockPosLower[z]};
		
		//The collision checking is done twice. The first time, it collides normally except it does separate 
		//collisions for the upper and lower box while also avoiding diagonals.
		//It does this because it creates a specific order of collision checks
		//which creates smoother collisions. The second time checks the 5x5
		//space around the player in no particular order
		//First collision check
		for(int i = blockPos[x]-1; i <= blockPos[x]+1; i++)
			for(int j = blockPos[y]-1; j <= blockPos[y]+1; j++)
				for(int k = blockPos[z]-1; k <= blockPos[z]+1; k++){
					int x2 = i-blockPos[x];
					int y2 = j-blockPos[y];
					int z2 = k-blockPos[z];
					// This will evaluate to false if the block at position ijk
					// is diagonal to the players current block position.
					// This means diagonal blocks won't cause collisions in this
					// loop.
					if((x2 != 0 && y2 == 0 && z2 == 0) || (x2 == 0 && y2 != 0 && z2 == 0) || (x2 == 0 && y2 == 0 && z2 != 0)){
						for(int hitboxIndex = 0; hitboxIndex < hitboxes.length; hitboxIndex++){
							// Note the single |. This prevents short circuiting.
							// If it wasn't there, the user could only collide
							// with one block and then 'collision' would be true
							// so the second part of the | statement would not
							// be executed. So only one collision would occur.
							collision = collision | planet.getBlock(i, j, k).collide(i,j,k, planet, this, hitboxIndex);
						}
						for(int hitboxIndex = 0; hitboxIndex < hitboxes.length; hitboxIndex++)
							collision = collision | planet.getBlock(i-diff[x], j-diff[y], k-diff[z]).collide(i-diff[x], j-diff[y], k-diff[z], planet, this, hitboxIndex);
					}
				}
		
		// Second collision check
		// Does collisions with the 5x5 space around the
		// player.
		for(int i = blockPos[x]-2; i <= blockPos[x]+2; i++)
			for(int j = blockPos[y]-2; j <= blockPos[y]+2; j++)
				for(int k = blockPos[z]-2; k <= blockPos[z]+2; k++){
					for(int hitboxIndex = 0; hitboxIndex < hitboxes.length; hitboxIndex++)
						collision = collision | planet.getBlock(i, j, k).collide(i,j,k, planet, this, hitboxIndex);
				}
		
		return collision;
	}
	//
	public void updateCollisionsAdvanced(Planet p){
		float spacing = .3f;
		//Resetting the collision statuses for each hitbox
		for(int i = 0; i < hitboxCollided.length; i++)
			hitboxCollided[i] = false;
		
		float[] dirNorm = Ops.normalize(dir);
		float mag = Ops.getLength(dir);
		float last = mag%spacing;
		mag -= last;
		OpsMut.subtract(pos, dir);
		float i;
		for(i = 0; i < mag; i += spacing){
			OpsMut.add(pos, Ops.multiply(dirNorm,spacing));
			if(updateCollisionsBasic(p)){
				return;
			}
		}
		OpsMut.add(pos, Ops.multiply(dirNorm,last));
		updateCollisions(p);
	}
	// Simply collides with every block in a 5x5 radius around the physics body.
	// This way is fast but is not preferable for use with the player because
	// catching on the edges of blocks sometimes occurs.
	public boolean updateCollisionsBasic(Planet p){
		boolean collision = false;
		
		for(int i = 0; i < hitboxCollided.length; i++)
			hitboxCollided[i] = false;
		float[] upperBox = getHitbox(1);
		float[] lowerBox = getHitbox(0);
		int[] blockPos = new int[]{Math.round(upperBox[x]), Math.round(upperBox[y]), Math.round(upperBox[z])};
		int[] blockPosLower = new int[]{Math.round(lowerBox[x]),Math.round(lowerBox[y]),Math.round(lowerBox[z])};
		
		int[] diff = new int[]{blockPos[x]-blockPosLower[x],blockPos[y]-blockPosLower[y],blockPos[z]-blockPosLower[z]};
		
		for(int i = blockPos[x]-2; i <= blockPos[x]+2; i++)
			for(int j = blockPos[y]-2; j <= blockPos[y]+2; j++)
				for(int k = blockPos[z]-2; k <= blockPos[z]+2; k++){
					for(int l = 0; l < hitboxes.length; l++)
						collision = collision | p.getBlock(i, j, k).collide(i,j,k, p, this, l);
				}
		
		return collision;
	}
}
