package org.oreon.demo.vk;

import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.GLFWInput;
import org.oreon.core.system.Window;
import org.oreon.core.vk.scene.VkCamera;
import org.oreon.system.vk.desktop.VKWindow;
import org.oreon.system.vk.desktop.VkRenderEngine;

public class SimpleVulkanDemo {
	
	public static void main(String[] args) {

		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		VkRenderEngine renderEngine = new VkRenderEngine();
		Window window = new VKWindow();
		GLFWInput input = new GLFWInput();
		
		window.setWidth(1280);
		window.setHeight(720);
		
		coreSystem.setRenderingEngine(renderEngine);
		coreSystem.setWindow(window);
		coreSystem.setInput(input);
		coreSystem.getScenegraph().setCamera(new VkCamera());

		coreEngine.init(coreSystem);
		
		coreEngine.start();
	}
}
