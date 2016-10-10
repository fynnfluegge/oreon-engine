package simulations.oceanSurface;

import modules.fastFourierTransform.TwiddleFactors;
import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.main.OpenGLDisplay;
import engine.main.Simulation;

public class Twiddles extends Simulation{
	
	private TwiddleFactors twiddles;
	private int N = 256;
	
	public static void main(String[] args) {

		Simulation simulation = new Twiddles();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "phillips spectrum");
		coreEngine.createWindow();
		coreEngine.init(simulation, fps);
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
		OpenGLDisplay.getInstance().setSceneTexture(twiddles.getTexture());
	}

}
