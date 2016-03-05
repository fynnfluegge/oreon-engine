package simulations.oceanSurface;

import engine.gui.GUI;
import engine.gui.GUIs.FPSDisplay;
import engine.main.CoreEngine;
import engine.renderer.water.WaterMaps;
import simulations.templates.BasicSimulation;

public class DisplacementMap extends BasicSimulation{
	
	private WaterMaps maps;

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
		maps = new WaterMaps(512);
	}
	
	public void render()
	{
		maps.render();
		setSceneTexture(maps.getFFT().getDy());
	}
}
