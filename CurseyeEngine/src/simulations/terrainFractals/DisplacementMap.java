package simulations.terrainFractals;

import modules.gui.GUI;
import modules.gui.GUIs.FPSDisplay;
import modules.terrain.fractals.FractalMaps;
import simulations.templates.BasicSimulation;
import engine.main.CoreEngine;

public class DisplacementMap extends BasicSimulation{
	
	private FractalMaps maps;

public static void main(String[] args) {
		
		BasicSimulation simulation = new DisplacementMap();
		GUI fps = new FPSDisplay();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		maps = new FractalMaps(512,1f,400,10,1000);
	}
	
	public void render()
	{
		setSceneTexture(maps.getHeightmap());
	}
}
