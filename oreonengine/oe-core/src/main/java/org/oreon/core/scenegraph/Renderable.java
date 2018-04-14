 package org.oreon.core.scenegraph;

import java.util.HashMap;

import org.oreon.core.context.EngineContext;

public class Renderable extends Node{

	private HashMap<NodeComponentType, NodeComponent> components;
	
	public Renderable()
	{
		super();
		components = new HashMap<NodeComponentType, NodeComponent>();
	}
	
	public void addComponent(NodeComponentType type, NodeComponent component)
	{
		component.setParent(this);
		components.put(type, component);
	}
	
	public void update()
	{	
		for (NodeComponentType key : components.keySet()) {
			components.get(key).update();
		}
		super.update();
	}
	
	public void input()
	{
		for (NodeComponentType key : components.keySet()) {
			components.get(key).input();
		}
		
		super.input();
	}
	
	public void render()
	{
		if (EngineContext.getConfig().isWireframe()){
			if (components.containsKey(NodeComponentType.WIREFRAME_RENDERINFO)){
				components.get(NodeComponentType.WIREFRAME_RENDERINFO).render();
			}
		}
		else{
			components.get(NodeComponentType.MAIN_RENDERINFO).render();
		}
		
		super.render();
	}
	
	public void renderShadows()
	{
		if (components.containsKey(NodeComponentType.SHADOW_RENDERINFO)){
			components.get(NodeComponentType.SHADOW_RENDERINFO).render();
		}
		
		super.renderShadows();
	}

	public HashMap<NodeComponentType, NodeComponent> getComponents() {
		return components;
	}
	
	public void setComponent(NodeComponentType type, NodeComponent component) {
		this.components.replace(type, component);
	}
	
	public NodeComponent getComponent(NodeComponentType type)
	{
		return this.components.get(type);
	}
}
