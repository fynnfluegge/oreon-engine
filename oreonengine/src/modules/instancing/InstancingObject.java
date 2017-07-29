package modules.instancing;

import java.util.ArrayList;
import java.util.List;

import engine.scene.Node;

public abstract class InstancingObject extends Node implements Runnable{
	
	private List<InstancedDataObject> objectData = new ArrayList<InstancedDataObject>();
	private List<InstancingCluster> clusters = new ArrayList<InstancingCluster>();
	
	private Thread thread;
	private boolean isRunning = true;
	
	public void shutdown() {
		setRunning(false);
	}
	
	public List<InstancedDataObject> getObjectData() {
		return objectData;
	}

	public void setObjectData(List<InstancedDataObject> objectData) {
		this.objectData = objectData;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
	public List<InstancingCluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<InstancingCluster> clusters) {
		this.clusters = clusters;
	}
	
	public void addCluster(InstancingCluster cluster){
		getClusters().add(cluster);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}