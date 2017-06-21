package org.oreon.engine.engine.scenegraph.components;

import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.shaders.Shader;

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
