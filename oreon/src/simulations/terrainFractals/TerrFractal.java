package simulations.terrainFractals;

import modules.gui.GUI;
import modules.gui.GUIs.FPSDisplay;
import modules.terrain.fractals.FractalMaps;
import engine.main.CoreEngine;
import engine.main.OpenGLDisplay;
import engine.main.Simulation;

public class TerrFractal extends Simulation{
	
	private FractalMaps maps;

public static void main(String[] args) {
		
		TerrFractal terrFractal = new TerrFractal();
		GUI fps = new FPSDisplay();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform");
		coreEngine.createWindow();
		coreEngine.init(terrFractal, fps);
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		maps = new FractalMaps(512,1f,400,10,1000);
	}
	
	public void render()
	{
		OpenGLDisplay.getInstance().setSceneTexture(maps.getHeightmap());
	}
}
