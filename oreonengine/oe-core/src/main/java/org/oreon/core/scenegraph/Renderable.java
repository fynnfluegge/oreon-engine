 package org.oreon.core.scenegraph;

import java.util.HashMap;

import org.oreon.core.context.EngineContext;

public class Renderable extends Node{

	private HashMap<NodeComponentKey, NodeComponent> components;
	
	public Renderable()
	{
		super();
		
		components = new HashMap<NodeComponentKey, NodeComponent>();
	}
	
	public void addComponent(NodeComponentKey type, NodeComponent component)
	{
		component.setParent(this);
		components.put(type, component);
	}
	
	public void update()
	{	
		for (NodeComponentKey key : components.keySet()) {
			components.get(key).update();
		}
		super.update();
	}
	
	public void input()
	{
		for (NodeComponentKey key : components.keySet()) {
			components.get(key).input();
		}
		
		super.input();
	}
	
	public void render()
	{
		if (EngineContext.getConfig().isWireframe()){
			if (components.containsKey(NodeComponentKey.WIREFRAME_RENDERINFO)){
				components.get(NodeComponentKey.WIREFRAME_RENDERINFO).render();
			}
		}
		else{
			components.get(NodeComponentKey.MAIN_RENDERINFO).render();
		}
		
		super.render();
	}
	
	public void renderShadows()
	{
		if (components.containsKey(NodeComponentKey.SHADOW_RENDERINFO)){
			components.get(NodeComponentKey.SHADOW_RENDERINFO).render();
		}
		
		super.renderShadows();
	}
	
	public void shutdown(){
		
		for (NodeComponentKey key : components.keySet()) {
			components.get(key).shutdown();
		}
		
		super.shutdown();
	}

	public HashMap<NodeComponentKey, NodeComponent> getComponents() {
		return components;
	}
	
	public NodeComponent getComponent(NodeComponentKey type) {
		return this.components.get(type);
	}
}
