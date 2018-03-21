package org.oreon.modules.gl.terrain;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.Component;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Node;

public class TerrainQuadtree extends Node implements Runnable{
	
	private Thread thread;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private boolean isRunning = false;
	
	private int updateCounter = 0;
	
	private static int rootPatches = 8;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig, HashMap<ComponentType, Component> components){
		
		for (int i=0; i<rootPatches; i++){
			for (int j=0; j<rootPatches; j++){
				addChild(new TerrainNode(components, terrConfig, new Vec2f(1f * i/(float)rootPatches,1f * j/(float)rootPatches), 0, new Vec2f(i,j)));
			}
		}
		
		getWorldTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
		getWorldTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setY(0);
		
		thread = new Thread(this);
	}	
	
	public void updateQuadtree(){
		
		updateCounter++;
		
		if (updateCounter == 4){
			
			for (Node node : getChildren()){
				((TerrainNode) node).updateQuadtree();
			}
			
			updateCounter = 0;
		}
	}
	
	public void start(){
		thread.start();
	}
	
	@Override
	public void run(){
		
		isRunning = true;
		
		while(isRunning){
			
			lock.lock();
			try{
				condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				lock.unlock();
			}
			
			updateQuadtree();
		}
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
	
	@Override
	public void shutdown() {
		
		isRunning = false;
	};
	
	public static int getRootPatches() {
		return rootPatches;
	}

}
