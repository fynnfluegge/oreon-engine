package org.oreon.gl.demo.oreonworlds;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.gui.GUI;
import org.oreon.gl.demo.oreonworlds.water.Ocean;
import org.oreon.gl.engine.GLRenderEngine;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.atmosphere.Sun;

public class Main {

	public static void main(String[] args) {

		EngineContext.initialize();
		GLContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		
		GLRenderEngine renderEngine = new GLRenderEngine();
		renderEngine.setGui(new GUI());
		
		CoreSystem coreSystem = new CoreSystem();
		coreSystem.setRenderEngine(renderEngine);
		coreEngine.init(coreSystem);
		
//		coreSystem.getScenegraph().setTerrain(new Terrain(TerrainShader.getInstance(),
//													      TerrainWireframeShader.getInstance(), 
//													      null));
		
		renderEngine.getScenegraph().addObject(new SkySphere());	
		renderEngine.getScenegraph().addTransparentObject(new Sun());
		renderEngine.getScenegraph().setWater(new Ocean());
		
//		coreSystem.getScenegraph().getRoot().addChild(new Bush01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Plant01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Grass01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
		
		coreEngine.start();
	}

}
