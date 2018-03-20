package org.oreon.core.terrain;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.oreon.core.scenegraph.Node;

public class Terrain extends Node implements Runnable{
	
	private Thread thread;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private boolean isRunning = false;

	@Override
	public void run(){};
	
	@Override
	public void shutdown() {
		
		setRunning(false);
	};
	
	public void signal(){
		
		lock.lock();
		try{
			condition.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
	public float getTerrainHeight(float x, float y){
		return 0;
	}
	
	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
