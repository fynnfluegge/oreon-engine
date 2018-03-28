package org.oreon.core.system;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.oreon.core.context.EngineContext;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;


public class CoreSystem {

	private Window window;
	private Input input;
	private RenderEngine renderEngine;
	
	private GLFWErrorCallback errorCallback;
	
	public CoreSystem() {
		
		window = EngineContext.getWindow();
		input = EngineContext.getInput();
	}
	
	public void init(){
		
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		window.create();
		input.create(window.getId());
		renderEngine.init();
	}
	
	public void update(){
		
		input.update();
		renderEngine.update();
	}
	
	public void render(){
		
		renderEngine.render();
		window.draw();
	}
	
	public void shutdown(){
		
		window.shutdown();
		input.shutdown();
		renderEngine.shutdown();
		errorCallback.free();
		glfwTerminate();
	}

	public void setRenderEngine(RenderEngine renderingEngine) {
		this.renderEngine = renderingEngine;
	}
}
