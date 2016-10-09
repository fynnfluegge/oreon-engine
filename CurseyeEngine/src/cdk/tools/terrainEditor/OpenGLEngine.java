package cdk.tools.terrainEditor;

import java.awt.Canvas;

import cdk.terrain.Terrain;
import modules.gui.GUI;
import modules.gui.GUIs.EngineGUI;
import modules.sky.SkySphere;
import engine.main.CoreEngine;
import engine.main.Simulation;

public class OpenGLEngine extends Simulation implements Runnable{
	
	Canvas OpenGLCanvas;
	
	public OpenGLEngine(Canvas canvas){
		OpenGLCanvas = canvas;
	}
	
	public void init()
	{	
		super.init();
		scenegraph.setTerrain(new Terrain());
		scenegraph.addObject(new SkySphere());		
	}

	@Override
	public void run() {
		
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(1200, 350, "TerrainLoader");
		coreEngine.embedWindow(OpenGLCanvas);
		coreEngine.init(this, gui);
		coreEngine.start();
	}
}
