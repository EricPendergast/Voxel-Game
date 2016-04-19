package framework;
/**
 * A thread that doesn't have to iterate in a specific amount of time
 */
public abstract class BetaThread implements Runnable {
	public final Thread myThread;
	public BetaThread(){
		myThread = new Thread(this);
	}
	public BetaThread(String threadName){
		this();
		myThread.setName(threadName);
	}
	public abstract void run();
	
	public void pause(){
		if(myThread.isAlive())
			try {
				myThread.wait();
			} catch (InterruptedException e) {}
	}
	public void resume(){
		if(myThread.isAlive())
			myThread.notify();
	}
	
	public void start(){
		myThread.start();
	}
}
