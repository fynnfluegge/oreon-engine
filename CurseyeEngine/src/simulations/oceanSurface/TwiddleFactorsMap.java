package simulations.oceanSurface;

import simulations.templates.BasicSimulation;
import engine.gui.GUI;
import engine.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.renderer.water.TwiddleFactors;

public class TwiddleFactorsMap extends BasicSimulation{
	
	private TwiddleFactors butterflyData;
	private int N = 256;
	
	public static void main(String[] args) {

		BasicSimulation simulation = new TwiddleFactorsMap();
		GUI gui = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();
	}
	
	public void init()
	{	
		super.init();
		butterflyData = new TwiddleFactors(N);
		butterflyData.renderToTexture();
	}
	
	
	public void render()
	{
		setSceneTexture(butterflyData.getTexture());
	}

}
