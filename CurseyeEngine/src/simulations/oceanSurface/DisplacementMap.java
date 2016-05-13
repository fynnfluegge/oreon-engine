package simulations.oceanSurface;

import modules.water.WaterMaps;
import engine.gui.GUI;
import engine.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import simulations.templates.BasicSimulation;

public class DisplacementMap extends BasicSimulation{
	
	private WaterMaps maps;

public static void main(String[] args) {
		
		BasicSimulation simulation = new DisplacementMap();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		maps = new WaterMaps(256);
	}
	
	public void render()
	{
		maps.render();
		setSceneTexture(maps.getFFT().getDy());
	}
}
