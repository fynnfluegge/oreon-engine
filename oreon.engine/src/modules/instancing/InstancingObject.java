package modules.instancing;


import java.util.ArrayList;
import java.util.List;

import engine.scenegraph.GameObject;
import engine.scenegraph.Node;

public class InstancingObject extends Node{

	private List<GameObject> objects = new ArrayList<GameObject>();

	public List<GameObject> getObjects() {
		return objects;
	}
}
