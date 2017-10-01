package org.oreon.demo.ocean;

import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.Window;
import org.oreon.modules.atmosphere.SkySphere;
import org.oreon.modules.atmosphere.Sun;
import org.oreon.system.desktop.GLFWInput;
import org.oreon.system.desktop.GLRenderingEngine;
import org.oreon.system.desktop.GLWindow;

public class Main {

	public static void main(String[] args) {
		
		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLRenderingEngine renderingengine = new GLRenderingEngine();
		Window window = new GLWindow();
		
		renderingengine.setGui(new GUI());
		window.setWidth(1280);
		window.setHeight(720);
		window.setTitle("OREON ENGINE ocean");
		
		coreSystem.setRenderingEngine(renderingengine);
		coreSystem.setWindow(window);
		coreSystem.setInput(new GLFWInput());
		coreSystem.getScenegraph().setCamera(new GLCamera());
		
		coreEngine.init(coreSystem);
		
		coreSystem.getScenegraph().addObject(new SkySphere());	
		coreSystem.getScenegraph().addObject(new Sun());
		coreSystem.getScenegraph().setWater(new Ocean());
		
		coreEngine.start();
	}
}
