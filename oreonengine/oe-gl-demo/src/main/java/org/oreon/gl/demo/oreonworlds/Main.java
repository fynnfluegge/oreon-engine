package org.oreon.gl.demo.oreonworlds;

import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.assets.plants.Palm01ClusterGroup;
import org.oreon.gl.demo.oreonworlds.assets.plants.Tree01ClusterGroup;
import org.oreon.gl.demo.oreonworlds.assets.plants.Tree02ClusterGroup;
import org.oreon.gl.demo.oreonworlds.assets.rocks.Rock01ClusterGroup;
import org.oreon.gl.demo.oreonworlds.assets.rocks.Rock02ClusterGroup;
import org.oreon.gl.demo.oreonworlds.gui.GUI;
import org.oreon.gl.demo.oreonworlds.shaders.TerrainGridShader;
import org.oreon.gl.demo.oreonworlds.shaders.TerrainShader;
import org.oreon.gl.demo.oreonworlds.water.Ocean;
import org.oreon.gl.engine.GLRenderEngine;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.atmosphere.Sun;
import org.oreon.modules.gl.terrain.GLTerrain;

public class Main {

	public static void main(String[] args) {

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
		coreSystem.getScenegraph().setCamera(new GLCamera(input));

		coreEngine.init(coreSystem);
		
		GLTerrain terrain = new GLTerrain();
		terrain.init("oreonworlds/terrain/terrain_settings.txt",
				   "oreonworlds/terrain/terrain_settings_LowPoly.txt",
				   TerrainShader.getInstance(),
				   TerrainGridShader.getInstance(), 
				   null);
		coreSystem.getScenegraph().setTerrain(terrain);
		
		coreSystem.getScenegraph().addObject(new SkySphere());	
		coreSystem.getScenegraph().addTransparentObject(new Sun());
//		coreSystem.getScenegraph().setWater(new Ocean());
		
//		coreSystem.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
		
		coreEngine.start();
	}

}
