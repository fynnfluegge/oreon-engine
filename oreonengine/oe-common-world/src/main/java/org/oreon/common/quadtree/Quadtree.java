package org.oreon.common.quadtree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.RenderList;

import lombok.Getter;

public abstract class Quadtree extends Node implements Runnable{

	private Thread thread;
	private Lock startUpdateQuadtreeLock;
	private Lock finishUpdateQuadtreeLock;
	private Condition startUpdateQuadtreeCondition;
	private boolean isRunning;
	private boolean updateQuadtreeFinished;
	private int updateCounter;
	private int updateThreshold = 2;
	protected ConcurrentHashMap<String, QuadtreeChunk> leafChunks;
	private ConcurrentHashMap<String, QuadtreeChunk> formerLeafChunks;
	
	@Getter
	protected QuadtreeCache quadtreeCache;
	
	public Quadtree() {
		isRunning = false;
		updateQuadtreeFinished = true;
		startUpdateQuadtreeLock = new ReentrantLock();
		finishUpdateQuadtreeLock = new ReentrantLock(); 
		startUpdateQuadtreeCondition = startUpdateQuadtreeLock.newCondition();
		thread = new Thread(this);
		quadtreeCache = new QuadtreeCache();
		leafChunks = new ConcurrentHashMap<String, QuadtreeChunk>();
		formerLeafChunks = new ConcurrentHashMap<String, QuadtreeChunk>();
	}
	
	public void updateQuadtree(){
		
		if (EngineContext.getCamera().isCameraMoved()){
			updateCounter++;
		}
		
		if (updateCounter == updateThreshold){
			synchronized(finishUpdateQuadtreeLock) {
				updateQuadtreeFinished = false;
				for (Node node : getChildren()){
					((QuadtreeChunk) node).updateQuadtree(leafChunks, formerLeafChunks);
				}
				updateCounter = 0;
				updateQuadtreeFinished = true;
				finishUpdateQuadtreeLock.notifyAll();
			}
		}
	}
	
	public void start(){
		thread.start();
	}

	@Override
	public void run(){
		
		isRunning = true;
		
		while(isRunning){
			
			startUpdateQuadtreeLock.lock();
			try{
				startUpdateQuadtreeCondition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				startUpdateQuadtreeLock.unlock();
			}
			
			updateQuadtree();
		}
	};
	
	public void signal(){
		
		startUpdateQuadtreeLock.lock();
		try{
			startUpdateQuadtreeCondition.signal();
		}
		finally{
			startUpdateQuadtreeLock.unlock();
		}
	}
	
	@Override
	public void record(RenderList renderList){
		
		// wait on updateQuadtree() finish
		synchronized(finishUpdateQuadtreeLock) {
			while(!updateQuadtreeFinished) {
				try {
					finishUpdateQuadtreeLock.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			for (Map.Entry<String, QuadtreeChunk> entry : leafChunks.entrySet()){
				
				if (!renderList.contains(entry.getKey())){
					renderList.add(entry.getValue());
					renderList.setChanged(true);
				}
			}
			for (Map.Entry<String, QuadtreeChunk> entry : formerLeafChunks.entrySet()){
				
				if (renderList.contains(entry.getKey())){
					renderList.remove(entry.getValue());
					renderList.setChanged(true);
				}
			}
			formerLeafChunks.clear();
		}
	}
	
	@Override
	public void shutdown() {
		
		isRunning = false;
	};
	
	@Override
	public void update(){
		
		// wait on updateQuadtree() finish
		synchronized(finishUpdateQuadtreeLock) {
			while(!updateQuadtreeFinished) {
				try {
					finishUpdateQuadtreeLock.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for(Node child: getChildren())
				child.update();
		}
	}
	
	public abstract QuadtreeChunk createChildChunk(Map<NodeComponentType, NodeComponent> components,
			QuadtreeCache quadtreeCache, Transform worldTransform,
			Vec2f location, int levelOfDetail, Vec2f index);
	
}
