package framework;

public class NanoTimer{
	long start;
	public void reset(){
		 start = System.nanoTime();
	}
	public void start(){
		 start = System.nanoTime();
	}
	public long getTime(){
		return System.nanoTime() - start;
	}
	public long getMillis(){
		return (System.nanoTime() - start)/1000000;
	}
	public void println(){
		System.out.println(getTime());
	}
	public void println(String st){
		System.out.println(st + " " + getTime());
	}
}