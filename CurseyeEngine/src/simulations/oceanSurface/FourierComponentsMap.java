package simulations.oceanSurface;

import engine.gui.GUI;
import engine.gui.GUIs.FPSDisplay;
import engine.main.CoreEngine;
import engine.renderer.water.FourierComponents;
import simulations.templates.BasicSimulation;

public class FourierComponentsMap extends BasicSimulation{

	private FourierComponents fourierComponents;
	private float t;
	private int N = 256;
	private int L = 1000;
	
public static void main(String[] args) {
		
		BasicSimulation simulation = new FourierComponentsMap();
		GUI fps = new FPSDisplay();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		fourierComponents = new FourierComponents(N,L);
	}
	
	
	public void render()
	{
		t += 0.005;
		fourierComponents.update(t);
		
		setSceneTexture(fourierComponents.getPingpong0Dy());
	}
}
