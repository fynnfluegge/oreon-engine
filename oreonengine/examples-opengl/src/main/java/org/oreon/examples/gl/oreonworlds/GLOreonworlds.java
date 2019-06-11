package org.oreon.examples.gl.oreonworlds;

import org.oreon.core.gl.context.GLContext;
import org.oreon.examples.gl.oreonworlds.gui.GLSystemMonitor;
import org.oreon.examples.gl.oreonworlds.ocean.Ocean;
import org.oreon.examples.gl.oreonworlds.shaders.TerrainShader;
import org.oreon.examples.gl.oreonworlds.shaders.TerrainShadowShader;
import org.oreon.examples.gl.oreonworlds.shaders.TerrainWireframeShader;
import org.oreon.examples.gl.oreonworlds.terrain.Terrain;
import org.oreon.gl.components.atmosphere.Atmosphere;
import org.oreon.gl.engine.GLDeferredEngine;

public class GLOreonworlds {

	public static void main(String[] args) {

		GLContext.create();
		
		GLDeferredEngine renderEngine = new GLDeferredEngine(); 
		renderEngine.setGui(new GLSystemMonitor());
		renderEngine.init();
		
		renderEngine.getSceneGraph().addObject(new Atmosphere());	
		renderEngine.getSceneGraph().setWater(new Ocean());
		renderEngine.getSceneGraph().setTerrain(new Terrain(TerrainShader.getInstance(),
				TerrainWireframeShader.getInstance(), TerrainShadowShader.getInstance()));
		
		
		GLContext.setRenderEngine(renderEngine);
		GLContext.getCoreEngine().start();
		
//		renderEngine.getScenegraph().getRoot().addChild(new Bush01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Plant01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Grass01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
	}

}
