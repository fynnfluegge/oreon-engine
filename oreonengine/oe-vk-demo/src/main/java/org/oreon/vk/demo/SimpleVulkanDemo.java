package org.oreon.vk.demo;

import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.context.VkContext;
import org.oreon.vk.components.atmosphere.Skydome;
import org.oreon.vk.engine.VkRenderEngine;

public class SimpleVulkanDemo {
	
	public static void main(String[] args) {
		
		VkContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		VkRenderEngine renderEngine = new VkRenderEngine();
		
		CoreSystem coreSystem = new CoreSystem();
		coreSystem.setRenderEngine(renderEngine);
		coreEngine.init(coreSystem);

		renderEngine.getSceneGraph().addObject(new VkTestObject());
		renderEngine.getSceneGraph().addObject(new Skydome());
//		renderEngine.getSceneGraph().setWater(new Water());
		
		VkRenderEngine.createSwapChain();
		
		coreEngine.start();
	}
}
