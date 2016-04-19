package framework;

import java.util.ArrayList;

public class ThreadManager extends AlphaThread{
	/**
	 * An alpha thread is a thread which must do a whole iteration within a certain amount of time
	 */
	private ArrayList<AlphaThread> alphaThreads;
	/**
	 * A beta thread is a thread that doesn't need to complete a whole iteration within a certain amount of time
	 */
	private ArrayList<BetaThread> betaThreads;
	
	public ThreadManager(){
		super("Thread Manager");
		alphaThreads = new ArrayList<AlphaThread>();
		betaThreads = new ArrayList<BetaThread>();
		
	}
	public void waitForAlphaThreads(){
		//Timer tim = new Timer();
		//tim.start();
		boolean finishedRunning = false;
		while(!finishedRunning){
			finishedRunning = true;
			for(AlphaThread t : alphaThreads){
				if(!t.isFinished())
					finishedRunning = false;
			}
		}
		//System.out.println(tim.getTime());
	}
	/**
	 * 1. Pause all beta threads
	 * 2. Start all alpha threads
	 * 3. Wait until all alpha threads are finished
	 * 4. Start all beta threads
	 */
	protected void iterate() {
		for(BetaThread t : betaThreads){
			t.pause();
		}
		for(AlphaThread t : alphaThreads){
			t.resume();
		}
		boolean finishedRunning = false;
		while(!finishedRunning){
			if(alphaThreads.size() == 0)
				break;
			for(AlphaThread t : alphaThreads){
				if(t.isFinished())
					finishedRunning = true;
			}
		}
		
		for(BetaThread t : betaThreads){
			t.resume();
		}
	}
	//precondition: t is alive
	public void addAlphaThread(AlphaThread t){
		alphaThreads.add(t);
	}
	//precondition: t is alive
	public void addBetaThread(BetaThread t){
		betaThreads.add(t);
	}
	public void removeAlphaThread(AlphaThread t){
		alphaThreads.remove(t);
	}
	public void removeBetaThread(BetaThread t){
		betaThreads.remove(t);
	}
	public void start(){
		myThread.start();
	}
}
