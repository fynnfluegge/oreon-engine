 package engine.scenegraph;

import java.util.ArrayList;
import java.util.HashMap;

import engine.core.Transform;
import engine.scenegraph.components.Component;

public class GameObject {

	private GameObject parent;
	private ArrayList<GameObject> children;
	private HashMap<String, Component> components;
	private Transform transform;	
	
	public GameObject()
	{
		children = new ArrayList<GameObject>();
		setComponents(new HashMap<String, Component>());
		transform = new Transform();
	}
	
	public void addChild(GameObject child)
	{
		child.setParent(this);
		children.add(child);
	}
	
	public void addComponent(String string, Component component)
	{
		component.setParent(this);
		components.put(string, component);
	}
	
	public void update()
	{
		if(getParent() != null){
			getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
			getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
			getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
		}
			
		for (String key : components.keySet()) {
			components.get(key).update();
		}
		
		for(GameObject child: children)
			child.update();
	}
	
	public void input()
	{
		for (String key : components.keySet()) {
			components.get(key).input();
		}
		
		for(GameObject child: children)
			child.input();
	}
	
	public void render()
	{
		if (components.containsKey("Renderer"))
			components.get("Renderer").render();
		
		for(GameObject child: children)
			child.render();
	}
	
	public void shutdown()
	{
		for(GameObject child: children)
			child.shutdown();
	}

	public ArrayList<GameObject> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<GameObject> children) {
		this.children = children;
	}

	public GameObject getParent() {
		return parent;
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public HashMap<String, Component> getComponents() {
		return components;
	}

	public void setComponents(HashMap<String, Component> components) {
		this.components = components;
	}
	
	public void setComponent(String string, Component component) {
		this.components.replace(string, component);
	}
	
	public Component getComponent(String component)
	{
		return this.components.get(component);
	}
}
