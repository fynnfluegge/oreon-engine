package org.oreon.core.instanced;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.scenegraph.Node;

public abstract class InstancedObject extends Node implements Runnable{
	
	private List<InstancedCluster> clusters = new ArrayList<InstancedCluster>();
	
	private Thread thread;
	private boolean isRunning = true;
	
	public void shutdown() {
		setRunning(false);
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
	public List<InstancedCluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<InstancedCluster> clusters) {
		this.clusters = clusters;
	}
	
	public void addCluster(InstancedCluster cluster){
		getClusters().add(cluster);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}