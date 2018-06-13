package org.oreon.examples.gl.oreonworlds;

import org.oreon.core.gl.context.GLContext;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.examples.gl.oreonworlds.gui.GLSystemMonitor;
import org.oreon.examples.gl.oreonworlds.ocean.Ocean;
import org.oreon.gl.components.atmosphere.SkySphere;
import org.oreon.gl.components.atmosphere.Sun;
import org.oreon.gl.engine.GLRenderEngine;

public class Oreonworlds {

	public static void main(String[] args) {

		GLContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		
		GLRenderEngine renderEngine = new GLRenderEngine();
		renderEngine.setGui(new GLSystemMonitor());
		
		CoreSystem coreSystem = new CoreSystem();
		coreSystem.setRenderEngine(renderEngine);
		coreEngine.init(coreSystem);
		
		renderEngine.getScenegraph().addObject(new SkySphere());	
		renderEngine.getScenegraph().addTransparentObject(new Sun());
		renderEngine.getScenegraph().setWater(new Ocean());
//		renderEngine.getScenegraph().setTerrain(new Terrain(TerrainShader.getInstance(),
//				TerrainWireframeShader.getInstance(), null));
		
//		coreSystem.getScenegraph().getRoot().addChild(new Bush01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Plant01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Grass01ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		renderEngine.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
		
		coreEngine.start();
	}

}
