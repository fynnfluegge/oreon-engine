package simulations.oceanSurface;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import engine.gpcgpu.fastFourierTransform.TwiddleFactors;
import engine.main.CoreEngine;
import simulations.templates.BasicSimulation;

public class Twiddles extends BasicSimulation{
	
	private TwiddleFactors twiddles;
	private int N = 256;
	
	public static void main(String[] args) {

		BasicSimulation simulation = new Twiddles();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "phillips spectrum", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}
	
	public void init()
	{	
		super.init();
		twiddles = new TwiddleFactors(N);
		twiddles.render();	
	}
	
	public void render()
	{
		setSceneTexture(twiddles.getTexture());
	}

}
