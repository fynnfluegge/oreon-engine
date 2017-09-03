package org.oreon.demo.oreonworlds;

import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.Window;
import org.oreon.demo.oreonworlds.assets.plants.Bush01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.plants.Grass01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.plants.Palm01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.plants.Plant01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.plants.Tree01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.plants.Tree02ClusterGroup;
import org.oreon.demo.oreonworlds.assets.rocks.Rock01ClusterGroup;
import org.oreon.demo.oreonworlds.assets.rocks.Rock02ClusterGroup;
import org.oreon.demo.oreonworlds.gui.GUI;
import org.oreon.demo.oreonworlds.shaders.terrain.TerrainGridShader;
import org.oreon.demo.oreonworlds.shaders.terrain.TerrainShader;
import org.oreon.demo.oreonworlds.water.Ocean;
import org.oreon.modules.atmosphere.SkySphere;
import org.oreon.modules.atmosphere.Sun;
import org.oreon.modules.gui.GUIs.VoidGUI;
import org.oreon.modules.terrain.Terrain;
import org.oreon.system.desktop.GLFWInput;
import org.oreon.system.desktop.GLRenderingEngine;
import org.oreon.system.desktop.GLWindow;

public class Main {

	public static void main(String[] args) {
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLRenderingEngine renderingengine = new GLRenderingEngine();
		Window window = new GLWindow();
		
		renderingengine.setGui(new VoidGUI());
		window.setWidth(1280);
		window.setHeight(720);
		window.setTitle("OREON ENGINE oreonworlds");
		
		coreSystem.setRenderingEngine(renderingengine);
		coreSystem.setWindow(window);
		coreSystem.setInput(new GLFWInput());
		coreSystem.getScenegraph().setCamera(new GLCamera());
		
		coreEngine.init(coreSystem);
		coreSystem.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("oreonworlds/terrain/terrain_settings.txt",
								   "oreonworlds/terrain/terrain_settings_LowPoly.txt",
								   TerrainShader.getInstance(),
								   TerrainGridShader.getInstance(), 
								   null);
		coreSystem.getScenegraph().addObject(new SkySphere());	
		coreSystem.getScenegraph().addObject(new Sun());
		
//		coreSystem.getScenegraph().getRoot().addChild(new Grass01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Plant01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Bush01ClusterGroup());
//		
//		coreSystem.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		
//		coreSystem.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
//		coreSystem.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
//		
//		coreSystem.getScenegraph().setWater(new Ocean());
		
		coreEngine.start();
	}

}
