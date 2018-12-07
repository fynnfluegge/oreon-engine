package org.oreon.examples.vk.oreonworlds;

import org.oreon.core.vk.context.VkContext;
import org.oreon.vk.components.atmosphere.Skydome;
import org.oreon.vk.components.atmosphere.Sun;
import org.oreon.vk.components.water.Water;
import org.oreon.vk.engine.VkDeferredEngine;

public class VkOreonworlds {
	
	public static void main(String[] args) {
		
		VkContext.create();

		VkDeferredEngine renderEngine = new VkDeferredEngine();
		renderEngine.setGui(new VkSystemMonitor());
		renderEngine.init();
		
		renderEngine.getSceneGraph().addObject(new Skydome());
		renderEngine.getSceneGraph().addTransparentObject(new Sun());
		renderEngine.getSceneGraph().setWater(new Water());
//		renderEngine.getSceneGraph().setTerrain(new Planet());
		
		VkContext.setRenderEngine(renderEngine);
		VkContext.getCoreEngine().start();
	}
}
