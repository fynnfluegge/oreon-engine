package simulations.oceanSurface;

import engine.gui.GUI;
import engine.gui.GUIs.FPSDisplay;
import engine.main.CoreEngine;
import engine.renderer.water.FastFourierTransform;
import simulations.templates.BasicSimulation;

public class DisplacementMap extends BasicSimulation{
	
	private FastFourierTransform fft;

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
		fft = new FastFourierTransform();
		fft.init();
	}
	
	public void render()
	{
		fft.render();
		setSceneTexture(fft.getDy());
	}
}
