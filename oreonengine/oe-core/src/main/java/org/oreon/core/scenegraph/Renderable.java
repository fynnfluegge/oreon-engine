 package org.oreon.core.scenegraph;

import java.util.HashMap;

import org.oreon.core.context.EngineContext;

public class Renderable extends Node{

	private HashMap<ComponentType, Component> components;
	
	public Renderable()
	{
		super();
		components = new HashMap<ComponentType, Component>();
	}
	
	public void addComponent(ComponentType type, Component component)
	{
		component.setParent(this);
		components.put(type, component);
	}
	
	public void update()
	{	
		for (ComponentType key : components.keySet()) {
			components.get(key).update();
		}
		super.update();
	}
	
	public void input()
	{
		for (ComponentType key : components.keySet()) {
			components.get(key).input();
		}
		
		super.input();
	}
	
	public void render()
	{
		if (EngineContext.getCommonConfig().isWireframe()){
			if (components.containsKey(ComponentType.WIREFRAME_RENDERINFO)){
				components.get(ComponentType.WIREFRAME_RENDERINFO).render();
			}
		}
		else{
			components.get(ComponentType.MAIN_RENDERINFO).render();
		}
		
		super.render();
	}
	
	public void renderShadows()
	{
		if (components.containsKey(ComponentType.SHADOW_RENDERINFO)){
			components.get(ComponentType.SHADOW_RENDERINFO).render();
		}
		
		super.renderShadows();
	}

	public HashMap<ComponentType, Component> getComponents() {
		return components;
	}
	
	public void setComponent(ComponentType type, Component component) {
		this.components.replace(type, component);
	}
	
	public Component getComponent(ComponentType type)
	{
		return this.components.get(type);
	}
}
