package org.oreon.examples.vk.oreonworlds;

import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.vk.context.VkContext;
import org.oreon.vk.components.atmosphere.Skydome;
import org.oreon.vk.components.atmosphere.Sun;
import org.oreon.vk.engine.VkRenderEngine;

public class VkOreonworlds {
	
	public static void main(String[] args) {
		
		VkContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		VkRenderEngine renderEngine = new VkRenderEngine();
//		renderEngine.setGui(new VkSystemMonitor());
		
		CoreSystem coreSystem = new CoreSystem();
		coreSystem.setRenderEngine(renderEngine);
		coreEngine.init(coreSystem);

		renderEngine.getSceneGraph().addObject(new Skydome());
		renderEngine.getSceneGraph().addTransparentObject(new Sun());
//		renderEngine.getSceneGraph().setWater(new Water());
//		renderEngine.getSceneGraph().setTerrain(new Planet());
		
		coreEngine.start();
	}
}
