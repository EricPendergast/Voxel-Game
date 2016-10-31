 package framework;

public class Generator {
	
	public static long a = 5915587277L;
	public static long b = 1500450271L;
	public static long m = 103843L;
	public static long last = 854843;
	public static int getRandomNumber(){
		last = (int)(((a*last+b))%m);
		return (int)last;
	}
	public static int generate(){
		return getRandomNumber();
	}
	public static void seed(int s){
		last = s;
	}
	
	public static void addNum(int n){
		last += n;
		last *= a;
	}
}
