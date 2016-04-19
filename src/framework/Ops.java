package framework;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Ops {
	public static final int x = 0;
	public static final int y = 1;
	public static final int z = 2;
	
	public static final int radius = 3;
	public static Vector3f rotateVector3f(float rotation, Vector3f npos, Vector3f nrot) {
	   Matrix4f matrix = new Matrix4f();
	
	   Vector3f pos = new Vector3f(npos);
	
	   matrix.m03 = pos.x;
	   matrix.m13 = pos.y;
	   matrix.m23 = pos.z;
	
	   Vector3f rot = new Vector3f(nrot);
	
	   Matrix4f.rotate((float) Math.toRadians(rotation), rot, matrix, matrix);
	
	   return new Vector3f(matrix.m03, matrix.m13, matrix.m23);
	}
	public static float[] rotateAlt(float[] a, float[] b, float angle){
		//float la = Ops.getLength(a);
		//a = Ops.normalize(a);
		Vector3f v = rotateVector3f(angle, new Vector3f(a[x],a[y],a[z]), new Vector3f(b[x],b[y],b[z]));
		//v = (Vector3f) v.normalise().scale(la);
		return new float[]{v.x,v.y,v.z};
	}
	public static float getLength(float[] a){
		return (float)Math.sqrt(getLengthsq(a));
	}
	public static float getLengthsq(float[] a){
		return a[0]*a[0] + a[1]*a[1] + a[2]*a[2];
	}
	public static float[] normalize(float[] a){
		float[] arr = new float[3];
		float l = (float)Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);
		if(l != 0){
			arr[0] = a[0]/l;
			arr[1] = a[1]/l;
			arr[2] = a[2]/l;
		}
		return arr;
	}
	public static float[] add(float[] a, float[] b){
		float[] arr = {a[0]+b[0], a[1]+b[1], a[2]+b[2]};
		return arr;
	}
	public static float[] subtract(float[] a, float[] b){
		float[] arr = {a[0]-b[0], a[1]-b[1], a[2]-b[2]};
//		float[] arr = new float[a.length];
//		for(int i = 0; i < arr.length; i++){
//			arr[i] =a[i]-b[i];
//		}
		return arr;
	}
	public static float[] multiply(float[] a, float scalar){
		//float[] arr = {a[0] * scalar, a[1] * scalar, a[2] * scalar};
//		float[] arr = new float[a.length];
//		for(int i = 0; i < arr.length; i++){
//			arr[i] =a[i]*scalar;
//		}
//		arr[arr.length-1] = a[a.length-1];
		
		return new float[]{a[x]*scalar,a[y]*scalar,a[z]*scalar};
	}
	public static float dotProduct(float[] a, float[] b){
		return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
	}
	public static float[] crossProduct(float[] a, float[] b){
		float[] cross = new float[4];
		cross[0] = a[1]*b[2]-a[2]*b[1];
		cross[1] = a[2]*b[0]-a[0]*b[2];
		cross[2] = a[0]*b[1]-a[1]*b[0];
		cross[3] = 0;
		
		return cross;
	}
	//Rotates a around b 
	public static float[] rotate(float[] a, float[] b, float angle){		
		if(angle == 0){
			float[] r = {a[0],a[1],a[2]};
			return r;
		}
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		
		float[] cross = crossProduct(b,a);
		float dot = dotProduct(b,a);
		
		float[] ret = new float[3];
		ret[0] = a[0]*cos + cross[0]*sin + b[0]*dot*(1-cos);
		ret[1] = a[1]*cos + cross[1]*sin + b[1]*dot*(1-cos);
		ret[2] = a[2]*cos + cross[2]*sin + b[2]*dot*(1-cos);
		
		return ret;
	}
	
	public static float[][] newPolygon(float a1, float b1, float c1, float a2, float b2, float c2,float a3, float b3, float c3){
		float[] arr1 = {a1,b1,c1,1};
		float[] arr2 = {a2,b2,c2,1};
		float[] arr3 = {a3,b3,c3,1};
		
		float[][] ret = {arr1,arr2,arr3};
		return ret;
	}
	public static float distanceBetween(float[] a, float[] b){
		float sum = 0;
		for(int i = 0; i < a.length; i++){
			float temp = a[i]-b[i];
			sum += temp*temp;
		}
		
		return (float)Math.sqrt(sum);
	}
	public static float comparitiveDistance(float[] a, float[] b){
		float sum = 0;
		for(int i = 0; i < a.length; i++){
			float temp = a[i]-b[i];
			sum += temp*temp;
		}
		return sum;
	}
	public static String arrToString(float[] arr){
		if(arr == null)
			return "null";
		String ret = "[";
		for(int i = 0; i < arr.length; i++){
			ret+=arr[i];
			if(i!=arr.length-1)
				ret+=", ";
		}
		ret+="]";
		return ret;
	}
	public static float[] vecMatrixMult(float[] vec, float[][] mat){
		float[] ret = new float[vec.length];
		for(int i = 0; i < vec.length; i++){
			ret[i] = 0;
			for(int j = 0; j < mat[0].length; j++)
				ret[i] += mat[i][j]*vec[j];
			
		}
		return ret;
	}
	public static float[][] matrixMult(float[][] a, float[][] b){
		float[][] ret = new float[a.length][b[0].length];
		for(int i = 0; i < ret.length; i++){
			for(int j = 0; j < ret[i].length; j++){
				for(int k = 0; k < a[0].length; k++){
					ret[i][j] += a[i][k]*b[k][j];
				}
			}
		}
		return ret;
	}
	public static float[] averagePoint(float[][] points){
		float[] ret = {0,0,0,0};
		
		for(float[] p : points)
			ret = Ops.add(ret, p);
		return Ops.multiply(ret, 1.0f/points.length);
	}
	
	public static float[][] newRotationMatrix(float[] axis, float[] point, float rad){
		float u = axis[0];
		float v = axis[1];
		float w = axis[2];
		float L = u*u + v*v + w*w;
		float Ls = (float)Math.sqrt(L);
		float a = point[0];
		float b = point[1];
		float c = point[2];
		float cos = (float)Math.cos(rad);
		float sin = (float)Math.sin(rad);
		
		float[][] ret = new float[4][4];
		
		ret[0][0] = (u*u+(v*v+w*w)*cos)/L;
		ret[1][0] = (u*v*(1-cos)+w*Ls*sin)/L;
		ret[2][0] = (u*w*(1-cos)-v*Ls*sin)/L;
		
		ret[0][1] = (u*v*(1-cos)-w*Ls*sin)/L;
		ret[1][1] = (v*v+(u*u+w*w)*cos)/L;
		ret[2][1] = (v*w*(1-cos)+u*Ls*sin)/L;
		
		ret[0][2] = (u*w*(1-cos)+v*Ls*sin)/L;
		ret[1][2] = (v*w*(1-cos)-u*Ls*sin)/L;
		ret[2][2] = (w*w+(u*u+v*v)*cos)/L;
		
		
		ret[0][3] = ((a*(v*v+w*w)-u*(b*v+c*w))*(1-cos)+(b*w-c*v)*Ls*sin);
		ret[1][3] = ((b*(u*u+w*w)-v*(a*u+c*w))*(1-cos)+(c*u-a*w)*Ls*sin);
		ret[2][3] = ((c*(u*u+v*v)-w*(a*u+b*v))*(1-cos)+(a*v-b*u)*Ls*sin);
		ret[3][3] = 1;
		return ret;
	}
	
	public static float getAngleBetween(float[] a, float[] b){
		float dot = dotProduct(a,b);
		float ret = (float)Math.acos(dot/(getLength(a)*getLength(b)));
		
		if(!Float.isFinite(ret))
			ret = 0;
		
		if(ret == 0 && dot < 0)
			ret = 180;
		
		return ret;
	}
	
	public static void print(float[] arr){
		System.out.println(arrToString(arr));
	}
	//magnitude, asmuth, zenith
	public static float[] cartesianToSpherical(float[] dir){
		float[] ret = new float[3];
		float zenith = (float)Math.toDegrees(Math.acos(dir[y]/Math.sqrt(dir[z]*dir[z] + dir[x]*dir[x] + dir[y]*dir[y])));
		float azimuth = (float)Math.toDegrees(Math.atan(dir[x]/dir[z]));
		if(dir[z] < 0)
			azimuth = 180 + azimuth;
		
		float l = getLength(dir);
		
		ret[x] = l;
		ret[y] = azimuth;
		ret[z] = zenith;
		
		return ret;
	}
	
	
	//sets the vectors two smaller components to 0 and normalizes it
	public static float[] simplify(float[] arr){
		float mag = getLength(arr);
		float[] ret = new float[3];
		int maxIndex = 0;
		for(int i = 1; i < 3; i++)
			if(Math.abs(arr[maxIndex]) < Math.abs(arr[i]))
				maxIndex = i;
		
		for(int i = 0; i < 3; i++){
			if(i == maxIndex)
				ret[i] = arr[i] > 0 ? mag : -mag;
			else
				ret[i] = .00000001f ;
		}
		
		return ret;
	}
	
	public static float[] copy(float[] arr){
		float[] ret = new float[arr.length];
		for(int i = 0; i < ret.length; i++)
			ret[i] = arr[i];
		
		return ret;
	}
	public static int[] copy(int[] arr){
		int[] ret = new int[arr.length];
		for(int i = 0; i < ret.length; i++)
			ret[i] = arr[i];
		
		return ret;
	}
	/**
	 * Takes two cubes in the form (x,y,z, height/2) and returns a vector (x,y,z)
	 * that is the shortest direction 'cubeA' has to move to not be colliding with b
	 * returns null if cubeA and cubeB  don't intersect
	 * @param cubeA The first cube
	 * @param cubeB The second cube
	 * @return the direction cubeA must move to get out of the way of cubeB
	 */
	public static float[] collide(float[] cubeA, float[] cubeB){
		float sumRads = cubeA[3] + cubeB[3];
		
		for(int i = 0; i < 3; i++){
			if(Math.abs(cubeA[i] - cubeB[i]) > sumRads)
				return new float[3];
		}
		//all the possible directions (and magnitudes) cubeA could move in to not be coliding with the cubeB,
		//where the index is the direction (0,1-x , 2,3-y , 4,5-z) and the value is the magnitude 
		float[] possibleDirs = new float[6];
		
		for(int i = 0; i < 6; i++){
			//difference between the centers of the two cubes
			possibleDirs[i] = cubeB[i/2] - cubeA[i/2];
			//subtracting the addition of the two radiuses yields the difference between the edges of the cubes
			possibleDirs[i] -= sumRads*(i%2==0 ? 1 : -1);
		}
		
		//finding the index of the smallest value in 'possibleDirs'
		int minIndex = 0;
		for(int i = 1; i < 6; i++)
			minIndex = Math.abs(possibleDirs[i]) < Math.abs(possibleDirs[minIndex]) ? i : minIndex;
		
		float[] ret = new float[3];
		ret[minIndex/2] = possibleDirs[minIndex];
		
		return Ops.multiply(ret, 1);
	}
	
	public boolean areIntersecting(float[] a, float[] b){
		float sumRads = a[3] + b[3];
		
		for(int i = 0; i < 3; i++){
			if(Math.abs(a[i] - b[i]) > sumRads)
				return false;
		}
		return true;
	}
}
