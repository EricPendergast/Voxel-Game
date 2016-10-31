package framework;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class OpsMut {
	
	public static float[] normalize(float[] a){
		float l = (float)Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);
		if(l != 0){
			a[0] = a[0]/l;
			a[1] = a[1]/l;
			a[2] = a[2]/l;
		}
		return a;
	}
	public static float[] add(float[] a, float[] b){
		a[0] += b[0];
		a[1] += b[1];
		a[2] += b[2];
		return a;
	}
	public static float[] subtract(float[] a, float[] b){
		a[0] -= b[0];
		a[1] -= b[1];
		a[2] -= b[2];
		return a;
	}
	public static float[] multiply(float[] a, float scalar){
		a[0] *= scalar;
		a[1] *= scalar;
		a[2] *= scalar;
		return a;
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
//	//Rotates a around b 
//	public static float[] rotate(float[] a, float[] b, float angle){
//		if(angle == 0){
//			float[] r = {a[0],a[1],a[2],a[3]};
//			return r;
//		}
//		float cos = (float)Math.cos(angle);
//		float sin = (float)Math.sin(angle);
//		
//		float[] cross = crossProduct(b,a);
//		float dot = dotProduct(b,a);
//		
//		float[] ret = new float[4];
//		ret[0] = a[0]*cos + cross[0]*sin + b[0]*dot*(1-cos);
//		ret[1] = a[1]*cos + cross[1]*sin + b[1]*dot*(1-cos);
//		ret[2] = a[2]*cos + cross[2]*sin + b[2]*dot*(1-cos);
//		ret[3] = 1;
//		return ret;
//	}
	
//	public static float[][] newPolygon(float a1, float b1, float c1, float a2, float b2, float c2,float a3, float b3, float c3){
//		float[] arr1 = {a1,b1,c1,1};
//		float[] arr2 = {a2,b2,c2,1};
//		float[] arr3 = {a3,b3,c3,1};
//		
//		float[][] ret = {arr1,arr2,arr3};
//		return ret;
//	}
	public static float distanceBetween(float[] a, float[] b){
		float sum = 0;
		for(int i = 0; i < a.length-1; i++){
			float temp = a[i]-b[i];
			sum += temp*temp;
		}
		
		return (float)Math.sqrt(sum);
	}
	public static float comparitiveDistance(float[] a, float[] b){
		float sum = 0;
		for(int i = 0; i < a.length-1; i++){
			float temp = a[i]-b[i];
			sum += temp*temp;
		}
		return sum;
	}
	public static String arrToString(float[] arr){
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
	
	//sets the vectors two smaller components to 0 and normalizes it
	public static float[] simplify(float[] arr){
		float mag = Ops.getLength(arr);
		int maxIndex = 0;
		for(int i = 1; i < 3; i++)
			if(Math.abs(arr[maxIndex]) < Math.abs(arr[i]))
				maxIndex = i;
		
		for(int i = 0; i < 3; i++){
			if(i == maxIndex)
				arr[i] = arr[i] > 0 ? mag : -mag;
			else
				arr[i] = .000000001f;
		}
		
		return arr;
	}
}
