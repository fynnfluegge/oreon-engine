package org.oreon.examples.gl.oreonworlds;

import org.oreon.core.gl.context.GLContext;
import org.oreon.examples.gl.oreonworlds.gui.GLSystemMonitor;
import org.oreon.examples.gl.oreonworlds.terrain.Terrain;
import org.oreon.gl.components.atmosphere.Atmosphere;
import org.oreon.gl.components.terrain.shader.TerrainShader;
import org.oreon.gl.components.terrain.shader.TerrainShadowShader;
import org.oreon.gl.components.terrain.shader.TerrainWireframeShader;
import org.oreon.gl.engine.GLDeferredEngine;

public class GLOreonworlds {

	public static void main(String[] args) {

		GLContext.create();
		
		GLDeferredEngine renderEngine = new GLDeferredEngine(); 
		renderEngine.setGui(new GLSystemMonitor());
		renderEngine.init();
		
		renderEngine.getSceneGraph().addObject(new Atmosphere());	
//		renderEngine.getSceneGraph().setWater(new Ocean());
		renderEngine.getSceneGraph().setTerrain(new Terrain(TerrainShader.getInstance(),
				TerrainWireframeShader.getInstance(), TerrainShadowShader.getInstance()));
		
//		renderEngine.getSceneGraph().getRoot().addChild(new Palm01ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Plant01ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Grass01ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Tree02ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Tree01ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Rock01ClusterGroup());
//		renderEngine.getSceneGraph().getRoot().addChild(new Rock02ClusterGroup());
		
		GLContext.setRenderEngine(renderEngine);
		GLContext.getCoreEngine().start();
	}
}
