package modules.instancing;

import java.util.ArrayList;
import java.util.List;
import engine.scenegraph.Node;

public class InstancingObject extends Node{
	
	private List<InstancedDataObject> objectData = new ArrayList<InstancedDataObject>();

	public List<InstancedDataObject> getObjectData() {
		return objectData;
	}

	public void setObjectData(List<InstancedDataObject> objectData) {
		this.objectData = objectData;
	}
}
