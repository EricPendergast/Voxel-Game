package framework;

public class Timer{
	long start;
	public void reset(){
		 start = System.currentTimeMillis();
	}
	public void start(){
		 start = System.currentTimeMillis();
	}
	public long getTime(){
		return System.currentTimeMillis() - start;
	}
	public void println(){
		System.out.println(getTime());
	}
	public void println(String st){
		System.out.println(st + " " + getTime());
	}
}