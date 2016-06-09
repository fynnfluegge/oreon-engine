package cdk.tools.terrainEditor;

import java.awt.Canvas;

import modules.gui.GUI;
import modules.gui.GUIs.EngineGUI;
import modules.lighting.DirectionalLight;
import engine.main.CoreEngine;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import simulations.templates.TerrainSimulation;

public class OpenGLEngine extends TerrainSimulation implements Runnable{
	
	Canvas OpenGLCanvas;
	
	public OpenGLEngine(Canvas canvas){
		OpenGLCanvas = canvas;
	}
	
	public void init()
	{	
		super.init();
		setTerrain(new Terrain());
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(-4,-2,-1).normalize(), new Vec3f(0.04f,0.04f,0.04f), new Vec3f(1.0f, 0.95f, 0.87f), 1.5f));
	}

	@Override
	public void run() {
		
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(500, 550, "TerrainLoader", this, gui);
		coreEngine.embedWindow(OpenGLCanvas);
		coreEngine.start();
	}
}
