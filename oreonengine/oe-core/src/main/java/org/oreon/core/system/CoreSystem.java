package org.oreon.core.system;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.oreon.core.context.EngineContext;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.Scenegraph;


public class CoreSystem {

	private static CoreSystem instance = null;

	private Window window;
	private Input input;
	private Scenegraph scenegraph;
	private RenderEngine renderEngine;
	
	private GLFWErrorCallback errorCallback;
	
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
		
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		window.create();
		input.create(window.getId());
		scenegraph.getCamera().init();
		renderEngine.init();
	}
	
	public void update(){
		
		input.update();
		scenegraph.update();
		EngineContext.getCamera().update();
		renderEngine.update();
	}
	
	public void render(){
		
		renderEngine.render();
		window.draw();
	}
	
	public void shutdown(){
		
		window.shutdown();
		input.shutdown();
		
		// important to shutdown scenegraph before render-engine, since
		// thread safety of instancing clusters.
		// scenegraph sets isRunning to false, render-engine signals all
		// waiting threads to shutdown
		
		scenegraph.shutdown();
		renderEngine.shutdown();
		
		errorCallback.free();
		
		glfwTerminate();
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

	public RenderEngine getRenderEngine() {
		return renderEngine;
	}

	public void setRenderEngine(RenderEngine renderingEngine) {
		this.renderEngine = renderingEngine;
	}
}
