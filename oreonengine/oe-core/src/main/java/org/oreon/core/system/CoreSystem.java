package org.oreon.core.system;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.oreon.core.scene.Scenegraph;


public class CoreSystem {

	private static CoreSystem instance = null;

	private Window window;
	private Input input;
	private Scenegraph scenegraph;
	private RenderEngine renderingEngine;
	
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
		renderingEngine.init();
	}
	
	public void update(){
		
		input.update();
		scenegraph.update();
		scenegraph.getCamera().update();
		renderingEngine.update();
	}
	
	public void render(){
		
		renderingEngine.render();
		window.draw();
	}
	
	public void shutdown(){
		
		window.shutdown();
		input.shutdown();
		scenegraph.shutdown();
		renderingEngine.shutdown();
		
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

	public RenderEngine getRenderingEngine() {
		return renderingEngine;
	}

	public void setRenderingEngine(RenderEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}
}
