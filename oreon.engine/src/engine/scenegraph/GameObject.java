 package engine.scenegraph;

import static org.lwjgl.opengl.GL11.glViewport;

import java.util.HashMap;

import engine.core.Window;
import engine.scenegraph.components.Component;
import engine.scenegraph.components.RenderInfo;
import engine.utils.Constants;

public class GameObject extends Node{

	private HashMap<String, Component> components;
	private RenderInfo renderinfo;
	
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
		renderinfo.getConfig().enable();
		components.get("Renderer").render();
		renderinfo.getConfig().disable();
		
		super.render();
	}
	
	public void renderShadows()
	{
		if (renderinfo.isShadowCaster()){
			components.get("Renderer").setShader(renderinfo.getShadowShader());
			renderinfo.getConfig().enable();
			glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
			
			components.get("Renderer").render();
			
			glViewport(0,0,Window.getInstance().getWidth(), Window.getInstance().getHeight());
			renderinfo.getConfig().disable();
			components.get("Renderer").setShader(renderinfo.getShader());
		}
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

	public RenderInfo getRenderInfo() {
		return renderinfo;
	}

	public void setRenderInfo(RenderInfo renderinfo) {
		this.renderinfo = renderinfo;
	}
}
