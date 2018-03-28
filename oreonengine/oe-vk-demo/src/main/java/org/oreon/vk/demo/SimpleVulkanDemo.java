package org.oreon.vk.demo;

import org.oreon.core.context.EngineContext;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.core.context.VkCamera;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.platform.VkWindow;
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
		
		coreSystem.setRenderEngine(renderEngine);
		coreSystem.setWindow(window);
		coreSystem.setInput(input);

		coreEngine.init(coreSystem);
		
		coreEngine.start();
	}
}
