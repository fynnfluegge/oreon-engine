package org.oreon.vk.demo;

import org.oreon.core.context.EngineContext;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.vk.engine.VkRenderEngine;

public class SimpleVulkanDemo {
	
	public static void main(String[] args) {
		
		EngineContext.initialize();
		VkContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		VkRenderEngine renderEngine = new VkRenderEngine();
		
		CoreSystem coreSystem = new CoreSystem();
		coreSystem.setRenderEngine(renderEngine);
		coreEngine.init(coreSystem);
		
		coreEngine.start();
	}
}
