package org.oreon.core.instancing;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InstancingObjectHandler {
	
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();

	private static InstancingObjectHandler instance = null;
	
	public static InstancingObjectHandler getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new InstancingObjectHandler();
	    }
	      return instance;
	}
	
	protected InstancingObjectHandler(){
	}
	
	public void signalAll(){
		lock.lock();
		try{
			condition.signalAll();
		}
		finally{
			lock.unlock();
		}
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
