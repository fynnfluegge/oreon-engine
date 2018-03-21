package org.oreon.vk.demo;

import org.oreon.core.context.EngineContext;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.context.VkCamera;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.vk.engine.VkRenderEngine;

public class SimpleVulkanDemo {
	
	public static void main(String[] args) {
		
		EngineContext.initialize();
		VkContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		VkRenderEngine renderEngine = new VkRenderEngine();
		Window window = new VkWindow();
		GLFWInput input = new GLFWInput();
		
		window.setWidth(EngineContext.getRenderConfig().getDisplayWidth());
		window.setHeight(EngineContext.getRenderConfig().getDisplayHeight());
		
		coreSystem.setRenderEngine(renderEngine);
		coreSystem.setWindow(window);
		coreSystem.setInput(input);
		coreSystem.getScenegraph().setCamera(new VkCamera());
		coreSystem.getScenegraph().getCamera().setInput(input);

		coreEngine.init(coreSystem);
		
		coreEngine.start();
	}
}
