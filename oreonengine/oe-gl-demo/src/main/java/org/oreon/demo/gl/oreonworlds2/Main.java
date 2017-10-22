package org.oreon.demo.gl.oreonworlds2;

import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.Window;
import org.oreon.demo.gl.oreonworlds.gui.GUI;
import org.oreon.demo.gl.oreonworlds2.shaders.TerrainGridShader;
import org.oreon.demo.gl.oreonworlds2.shaders.TerrainShader;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.terrain.Terrain;
import org.oreon.system.gl.desktop.GLDeferredRenderingEngine;
import org.oreon.system.gl.desktop.GLFWInput;
import org.oreon.system.gl.desktop.GLWindow;

public class Main {

	public static void main(String[] args) {

		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLDeferredRenderingEngine renderingengine = new GLDeferredRenderingEngine();
		Window window = new GLWindow();
		
		renderingengine.setGui(new GUI());
		window.setWidth(1280);
		window.setHeight(720);
		window.setTitle("OREON ENGINE oreonworlds 2.0");
		
		coreSystem.setRenderingEngine(renderingengine);
		coreSystem.setWindow(window);
		coreSystem.setInput(new GLFWInput());
		coreSystem.getScenegraph().setCamera(new GLCamera());

		coreEngine.init(coreSystem);
		
		coreSystem.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("oreonworlds2/terrain/terrain_settings.txt",
								   "oreonworlds2/terrain/terrain_settings_LowPoly.txt",
								   TerrainShader.getInstance(),
								   TerrainGridShader.getInstance(), 
								   null);
		coreSystem.getScenegraph().addObject(new SkySphere());	
		
		coreEngine.start();
	}

}
