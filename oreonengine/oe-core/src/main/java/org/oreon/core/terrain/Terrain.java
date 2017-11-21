package org.oreon.core.terrain;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.oreon.core.scene.Node;

public class Terrain extends Node implements Runnable{
	
	private Thread thread;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();

	@Override
	public void run(){};
	
	public float getTerrainHeight(float x, float y){
		return 0;
	};
	
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
}
