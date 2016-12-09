package engine.scenegraph;

import java.util.ArrayList;
import java.util.List;

import engine.scenegraph.components.Transform;

public class Node {

	private Node parent;
	private List<Node> children;
	private Transform transform;
	
	public Node(){
		
		setTransform(new Transform());
		setChildren(new ArrayList<Node>());
	}
	
	public void addChild(Node child)
	{
		child.setParent(this);
		children.add(child);
	}
	
	public void updateAll()
	{
		
	}
	
	public void update()
	{
		getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
		getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
		getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
		
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

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}
}
