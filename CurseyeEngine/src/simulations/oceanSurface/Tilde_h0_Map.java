package simulations.oceanSurface;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import modules.water.fft.Tilde_h0;
import engine.main.CoreEngine;
import simulations.templates.BasicSimulation;

public class Tilde_h0_Map extends BasicSimulation{

	private Tilde_h0 phillipsComponents;
	private int N = 256;
	private int L = 1000;
	
	public static void main(String[] args) {

		BasicSimulation simulation = new Tilde_h0_Map();
		GUI fps = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(512, 512, "~h0(k)", simulation, fps);
		coreEngine.createWindow();
		coreEngine.start();
	}
	
	public void init()
	{	
		super.init();
		phillipsComponents = new Tilde_h0(N,L);
		phillipsComponents.render();
	}
	
	
	
	public void render()
	{
		setSceneTexture(phillipsComponents.geth0kminus());
	}

}
