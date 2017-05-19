package modules.instancing;

import java.util.ArrayList;
import java.util.List;

public class InstancingThreadHandler {
	
	List<InstancingObject> instancingObjects = new ArrayList<InstancingObject>();
	
	public InstancingThreadHandler(){
		
	}
	
	public void update(){
		for (InstancingObject insance : instancingObjects){
			insance.getThread().notify();
		}
	}
	
	public List<InstancingObject> getInstancingObjects() {
		return instancingObjects;
	}

	public void setInstancingObjects(List<InstancingObject> instancingObjects) {
		this.instancingObjects = instancingObjects;
	}
}
