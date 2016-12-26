package engine.scenegraph.components;

import engine.scenegraph.GameObject;
import engine.shader.Shader;

public abstract class Component{
	
	private GameObject parent;
	
	public void update(){};
	
	public void input(){};
	
	public void render(){};
	
	public void setShader(Shader shader){};

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