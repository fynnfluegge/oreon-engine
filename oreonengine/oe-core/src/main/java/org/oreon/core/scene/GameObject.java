 package org.oreon.core.scene;

import static org.lwjgl.opengl.GL11.glViewport;

import java.util.HashMap;

import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.Constants;

public class GameObject extends Node{

	private HashMap<String, Component> components;
	
	public GameObject()
	{
		super();
		components = new HashMap<String, Component>();
	}
	
	public void addComponent(String string, Component component)
	{
		component.setParent(this);
		components.put(string, component);
	}
	
	public void update()
	{	
		for (String key : components.keySet()) {
			components.get(key).update();
		}
		super.update();
	}
	
	public void input()
	{
		for (String key : components.keySet()) {
			components.get(key).input();
		}
		
		super.input();
	}
	
	public void render()
	{
		components.get("Renderer").render();
		
		super.render();
	}
	
	public void renderShadows()
	{
		glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
		if (components.containsKey(Constants.SHADOW_RENDERER_COMPONENT)){
			components.get(Constants.SHADOW_RENDERER_COMPONENT).render();
		}
		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight());
		
		super.renderShadows();
	}

	public HashMap<String, Component> getComponents() {
		return components;
	}
	
	public void setComponent(String string, Component component) {
		this.components.replace(string, component);
	}
	
	public Component getComponent(String component)
	{
		return this.components.get(component);
	}
}
