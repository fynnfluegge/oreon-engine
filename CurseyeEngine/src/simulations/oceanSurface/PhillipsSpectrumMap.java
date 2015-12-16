package simulations.oceanSurface;

import engine.gui.GUI;
import engine.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.renderer.water.PhillipsSpectrum;
import simulations.templates.BasicSimulation;

public class PhillipsSpectrumMap extends BasicSimulation{

	private PhillipsSpectrum phillipsComponents;
	private int N = 256;
	private int L = 1000;
	
	public static void main(String[] args) {

		BasicSimulation simulation = new PhillipsSpectrumMap();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}
	
	public void init()
	{	
		super.init();
		phillipsComponents = new PhillipsSpectrum(N,L);
		phillipsComponents.renderToTexture();
	}
	
	
	public void render()
	{
		setSceneTexture(phillipsComponents.geth0k());
	}

}
