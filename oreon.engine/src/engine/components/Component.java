package engine.components;

import engine.scene.GameObject;
import engine.scene.Transform;

public abstract class Component{
	
	private GameObject parent;
	
	public void update(){};
	
	public void input(){};
	
	public void render(){};
	
	public GameObject getParent() {
		return parent;
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
	}

	public Transform getTransform()
	{
		return getParent().getTransform();
	}
}