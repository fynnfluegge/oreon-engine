package org.oreon.examples.gl.oreonworlds;

import org.oreon.core.gl.context.GLContext;
import org.oreon.examples.gl.oreonworlds.gui.GLSystemMonitor;
import org.oreon.examples.gl.oreonworlds.ocean.Ocean;
import org.oreon.gl.components.atmosphere.SkySphere;
import org.oreon.gl.components.atmosphere.Sun;
import org.oreon.gl.engine.GLRenderEngine;

public class GLOreonworlds {

	public static void main(String[] args) {

		GLContext.create();
		
		GLRenderEngine renderEngine = new GLRenderEngine(); 
		renderEngine.setGui(new GLSystemMonitor());
		renderEngine.init();
		
		renderEngine.getSceneGraph().addObject(new SkySphere());	
		renderEngine.getSceneGraph().addTransparentObject(new Sun());
		renderEngine.getSceneGraph().setWater(new Ocean());
		
		
		GLContext.setRenderEngine(renderEngine);
		GLContext.getCoreEngine().start();
		
//		renderEngine.getSceneGraph().setTerrain(new Terrain(TerrainShader.getInstance(),
//				TerrainWireframeShader.getInstance(), null));
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
