package org.oreon.core.scene;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.math.Transform;

public class Node {

	private Node parent;
	private List<Node> children;
	private Transform worldTransform;
	private Transform localTransform;
	
	public Node(){
		
		setWorldTransform(new Transform());
		setLocalTransform(new Transform());
		setChildren(new ArrayList<Node>());
	}
	
	public void addChild(Node child)
	{
		child.setParent(this);
		children.add(child);
	}
	
	public void update()
	{
		getWorldTransform().setRotation(getWorldTransform().getLocalRotation().add(getParent().getWorldTransform().getRotation()));
		getWorldTransform().setTranslation(getWorldTransform().getLocalTranslation().add(getParent().getWorldTransform().getTranslation()));
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling().mul(getParent().getWorldTransform().getScaling()));
		
		for(Node child: children)
			child.update();
	}
	
	public void input()
	{
		for(Node child: children)
			child.input();
	}
	
	public void render()
	{
		for(Node child: children)
			child.render();
	}
	
	public void renderShadows()
	{
		for(Node child: children)
			child.renderShadows();
	}
	
	public void shutdown()
	{
		for(Node child: children)
			child.shutdown();
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Transform getWorldTransform() {
		return worldTransform;
	}

	public void setWorldTransform(Transform transform) {
		this.worldTransform = transform;
	}

	public Transform getLocalTransform() {
		return localTransform;
	}

	public void setLocalTransform(Transform localTransform) {
		this.localTransform = localTransform;
	}
}
