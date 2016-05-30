package simulations.oceanSurface;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import modules.water.fft.Tilde_hkt;
import engine.main.CoreEngine;
import simulations.templates.BasicSimulation;

public class Tilde_hkt_Map extends BasicSimulation{

	private Tilde_hkt fourierComponents;
	private float t;
	private int N = 256;
	private int L = 1000;
	
public static void main(String[] args) {
		
		BasicSimulation simulation = new Tilde_hkt_Map();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "~h(k,t)", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		fourierComponents = new Tilde_hkt(N,L);
		fourierComponents.getSpectrum().render();
	}
	
	public void render()
	{
		t += 0.005;
		fourierComponents.update(t);
		setSceneTexture(fourierComponents.getDyComponents());
	}
}
