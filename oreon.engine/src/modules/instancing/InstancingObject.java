package modules.instancing;

import java.util.ArrayList;
import java.util.List;
import engine.scenegraph.Node;

public class InstancingObject extends Node implements Runnable{
	
	private List<InstancedDataObject> objectData = new ArrayList<InstancedDataObject>();
	
	private List<Node> instances = new ArrayList<Node>();
	
	private Thread thread;

	public List<InstancedDataObject> getObjectData() {
		return objectData;
	}

	public void setObjectData(List<InstancedDataObject> objectData) {
		this.objectData = objectData;
	}

	public List<Node> getInstances() {
		return instances;
	}

	public void setInstances(List<Node> instances) {
		this.instances = instances;
	}

	@Override
	public void run() {
		for (int i = 0; i<1000; i++){
			System.out.println("####thread");
		}
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}
}
