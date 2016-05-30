package simulations.oceanSurface;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import modules.water.fft.OceanFFT;
import engine.main.CoreEngine;
import simulations.templates.BasicSimulation;

public class DisplacementMap extends BasicSimulation{
	
	private OceanFFT fft;

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
		
		fft = new OceanFFT(256);
		fft.init();
	}
	
	public void render()
	{
		fft.render();
		setSceneTexture(fft.getDy());
	}
}
