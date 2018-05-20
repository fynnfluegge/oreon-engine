 package org.oreon.core.scenegraph;

import java.util.HashMap;
import java.util.Map;

import org.oreon.core.context.EngineContext;

import lombok.Getter;

public class Renderable extends Node{

	private HashMap<NodeComponentType, NodeComponent> components;
	@Getter
	private boolean render;
	
	public Renderable()
	{
		super();
		
		render = true;
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
		if (EngineContext.getRenderState().isWireframe()){
			if (components.containsKey(NodeComponentType.WIREFRAME_RENDERINFO)){
				components.get(NodeComponentType.WIREFRAME_RENDERINFO).render();
			}
		}
		else{
			if (components.containsKey(NodeComponentType.MAIN_RENDERINFO)){
				components.get(NodeComponentType.MAIN_RENDERINFO).render();
			}
		}
		
		super.render();
	}
	
	public void renderWireframe(){
		
		if (components.containsKey(NodeComponentType.WIREFRAME_RENDERINFO)){
			components.get(NodeComponentType.WIREFRAME_RENDERINFO).render();
		}
		
		super.renderWireframe();
	}
	
	public void renderShadows()
	{
		if (components.containsKey(NodeComponentType.SHADOW_RENDERINFO)){
			components.get(NodeComponentType.SHADOW_RENDERINFO).render();
		}
		
		super.renderShadows();
	}
	
	public void record(RenderList renderList){

		if (render){
			if (!renderList.contains(id)){
				renderList.add(this);
			}
		}
		else {
			if (renderList.contains(id)){
				renderList.remove(this);
			}
		}
		
		super.record(renderList);
	}
	
	public void shutdown(){
		
		for (NodeComponentType key : components.keySet()) {
			components.get(key).shutdown();
		}
		
		super.shutdown();
	}

	public Map<NodeComponentType, NodeComponent> getComponents() {
		return components;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getComponent(NodeComponentType type) {
		return (T) this.components.get(type);
	}
}
