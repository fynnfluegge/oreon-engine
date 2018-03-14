package org.oreon.vk.demo;

import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.scene.VkCamera;
import org.oreon.vk.engine.VKRenderEngine;

public class SimpleVulkanDemo {
	
	public static void main(String[] args) {
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		VKRenderEngine renderEngine = new VKRenderEngine();
		Window window = new VkWindow();
		GLFWInput input = new GLFWInput();
		
		window.setWidth(1280);
		window.setHeight(720);
		
		coreSystem.setRenderEngine(renderEngine);
		coreSystem.setWindow(window);
		coreSystem.setInput(input);
		coreSystem.getScenegraph().setCamera(new VkCamera(input));

		coreEngine.init(coreSystem);
		
		coreEngine.start();
	}
}
