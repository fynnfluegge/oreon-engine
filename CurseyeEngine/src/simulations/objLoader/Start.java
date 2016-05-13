package simulations.objLoader;

import simulations.templates.Simulation;
import engine.gui.GUI;
import engine.gui.GUIs.FPSDisplay;
import engine.main.CoreEngine;

public class Start {

	public static void main(String[] args) {
		
		Simulation simulation = new ActionBox();
		GUI gui = new FPSDisplay();
		CoreEngine coreEngine = new CoreEngine(1400, 800, "ActionBox", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();

	}

}
