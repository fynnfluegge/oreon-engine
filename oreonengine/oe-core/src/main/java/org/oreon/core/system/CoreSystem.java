package org.oreon.core.system;

import org.oreon.core.scene.Scenegraph;

public class CoreSystem {

	private static CoreSystem instance = null;

	private Window window;
	private Input input;
	private Scenegraph scenegraph;
	
	private RenderingEngine renderingEngine;
	
	public static CoreSystem getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new CoreSystem();
	    }
	      return instance;
	}
	
	private CoreSystem() {
		scenegraph = new Scenegraph();
	}
	
	public void init(){
		window.create();
		input.create(window.getId());
		scenegraph.getCamera().init();
		renderingEngine.init();
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public Scenegraph getScenegraph() {
		return scenegraph;
	}

	public void setScenegraph(Scenegraph scenegraph) {
		this.scenegraph = scenegraph;
	}

	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

	public void setRenderingEngine(RenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}
}
