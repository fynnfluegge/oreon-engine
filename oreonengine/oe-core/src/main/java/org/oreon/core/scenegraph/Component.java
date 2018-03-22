package org.oreon.core.scenegraph;

import org.oreon.core.math.Transform;

public abstract class Component implements Cloneable{
	
	private Renderable parent;
	
	public void update(){};
	
	public void input(){};
	
	public void render(){};
	
	public Renderable getParent() {
		return parent;
	}

	public void setParent(Renderable parent) {
		this.parent = parent;
	}

	public Transform getTransform()
	{
		return getParent().getWorldTransform();
	}
	
	public Component clone() throws CloneNotSupportedException{
		return (Component) super.clone();
	}
}