package simulations.oceanSurface;

import engine.gui.GUI;
import engine.gui.GUIs.FPSDisplay;
import engine.main.CoreEngine;
import engine.renderer.water.fft.PhillipsFourierComponents;
import simulations.templates.BasicSimulation;

public class PhillipsFourierComponentsMap extends BasicSimulation{

	private PhillipsFourierComponents fourierComponents;
	private float t;
	private int N = 512;
	private int L = 1000;
	
public static void main(String[] args) {
		
		BasicSimulation simulation = new PhillipsFourierComponentsMap();
		GUI fps = new FPSDisplay();
		CoreEngine coreEngine = new CoreEngine(512, 512, "phillips fourier components", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		fourierComponents = new PhillipsFourierComponents(N,L);
		fourierComponents.getSpectrum().render();
	}
	
	public void render()
	{
		t += 0.005;
		fourierComponents.update(t);
		setSceneTexture(fourierComponents.getDyComponents());
	}
}
