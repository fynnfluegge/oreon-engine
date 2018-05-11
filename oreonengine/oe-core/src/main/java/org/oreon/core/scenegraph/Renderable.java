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
		if (EngineContext.getRenderState().isWireframe()){
			if (components.containsKey(NodeComponentKey.WIREFRAME_RENDERINFO)){
				components.get(NodeComponentKey.WIREFRAME_RENDERINFO).render();
			}
		}
		else{
			if (components.containsKey(NodeComponentKey.MAIN_RENDERINFO)){
				components.get(NodeComponentKey.MAIN_RENDERINFO).render();
			}
		}
		
		super.render();
	}
	
	public void renderWireframe(){
		
		if (components.containsKey(NodeComponentKey.WIREFRAME_RENDERINFO)){
			components.get(NodeComponentKey.WIREFRAME_RENDERINFO).render();
		}
		
		super.renderWireframe();
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
	
	@SuppressWarnings("unchecked")
	public <T> T getComponent(NodeComponentKey type) {
		return (T) this.components.get(type);
	}
}
