package org.oreon.gl.demo.oreonworlds;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLCamera;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.assets.plants.Palm01ClusterGroup;
import org.oreon.gl.demo.oreonworlds.gui.GUI;
import org.oreon.gl.demo.oreonworlds.shaders.TerrainWireframeShader;
import org.oreon.gl.demo.oreonworlds.shaders.TerrainShader;
import org.oreon.gl.demo.oreonworlds.terrain.Terrain;
import org.oreon.gl.demo.oreonworlds.water.Ocean;
import org.oreon.gl.engine.GLRenderEngine;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.atmosphere.Sun;
import org.oreon.modules.gl.terrain.GLTerrain;

public class Main {

	public static void main(String[] args) {

		EngineContext.initialize();
		GLContext.initialize();
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLRenderEngine renderEngine = new GLRenderEngine();
		Window window = new GLWindow();
		GLFWInput input = new GLFWInput();
		
		renderEngine.setGui(new GUI());
		window.setWidth(1280);
		window.setHeight(720);
		window.setTitle("OREON ENGINE oreonworlds 2.0");
		
		coreSystem.setRenderEngine(renderEngine);
		coreSystem.setWindow(window);
		coreSystem.setInput(input);
		coreSystem.getScenegraph().setCamera(new GLCamera());
		coreSystem.getScenegraph().getCamera().setInput(input);

		coreEngine.init(coreSystem);
		
//		GLTerrain terrain = new Terrain();
//		terrain.init(TerrainShader.getInstance(),
//				     TerrainWireframeShader.getInstance(), 
//				     null);
//		coreSystem.getScenegraph().setTerrain(terrain);
		
		coreSystem.getScenegraph().addObject(new SkySphere());	
		coreSystem.getScenegraph().addTransparentObject(new Sun());
		coreSystem.getScenegraph().setWater(new Ocean());
		
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
