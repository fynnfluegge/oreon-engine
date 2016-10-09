package simulations.oceanSurface;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import modules.water.fft.OceanFFT;
import engine.main.CoreEngine;
import engine.main.OpenGLDisplay;
import engine.main.Simulation;

public class DisplacementMap extends Simulation{
	
	private OceanFFT fft;

public static void main(String[] args) {
		
		DisplacementMap simulation = new DisplacementMap();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "fast fourier transform");
		coreEngine.createWindow();
		coreEngine.init(simulation, fps);
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
		OpenGLDisplay.getInstance().setSceneTexture(fft.getDy());
	}
}
