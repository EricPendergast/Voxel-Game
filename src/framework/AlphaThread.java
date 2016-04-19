package framework;
/**
 * A thread that needs to do a full iteration every tick.
 */
public abstract class AlphaThread implements Runnable{
	public final Thread myThread;
	private boolean finished;
	public AlphaThread(){
		finished = false;
		myThread = new Thread(this);
	}
	public AlphaThread(String threadName){
		this();
		myThread.setName(threadName);
	}
	public void run() {
		//synchronized(this){
			while(true){
//				System.out.println("it");
				finished = false;
				iterate();
				finished = true;
				//sleeps until it is interrupted by the resume() method
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
//				try {
//					myThread.wait();
//				} catch (InterruptedException e) {}
			}
		//}
	}
	/**
	 * This method should be called and finished to completion each tick
	 */
	protected abstract void iterate();
	public boolean isFinished(){
		return finished;
	}
	/**
	 * This method makes the thread call iterate() and run to completion
	 */
	public void resume(){
		//if(finished && myThread.isAlive()){
		//	myThread.notify();
 		//}
		if(finished && myThread.isAlive()){
			myThread.interrupt();
		}
		
	}
	
	public void start(){
		myThread.start();
	}
}