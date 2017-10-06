package org.oreon.demo.gl.oreonworlds;

import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.Window;
import org.oreon.demo.gl.oreonworlds.assets.plants.Bush01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.plants.Grass01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.plants.Palm01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.plants.Plant01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.plants.Tree01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.plants.Tree02ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.rocks.Rock01ClusterGroup;
import org.oreon.demo.gl.oreonworlds.assets.rocks.Rock02ClusterGroup;
import org.oreon.demo.gl.oreonworlds.shaders.terrain.TerrainGridShader;
import org.oreon.demo.gl.oreonworlds.shaders.terrain.TerrainShader;
import org.oreon.demo.gl.oreonworlds.water.Ocean;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.atmosphere.Sun;
import org.oreon.modules.gl.gui.GUIs.VoidGUI;
import org.oreon.modules.gl.terrain.Terrain;
import org.oreon.system.gl.desktop.GLFWInput;
import org.oreon.system.gl.desktop.GLForwardRenderer;
import org.oreon.system.gl.desktop.GLWindow;

public class Main {

	public static void main(String[] args) {
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLForwardRenderer renderingengine = new GLForwardRenderer();
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
		
		coreSystem.getScenegraph().getRoot().addChild(new Grass01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Plant01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Bush01ClusterGroup());
		
		coreSystem.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Tree01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
		
		coreSystem.getScenegraph().getRoot().addChild(new Rock01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Rock02ClusterGroup());
		
		coreSystem.getScenegraph().setWater(new Ocean());
		
		coreEngine.start();
	}

}
