package simulations.objLoader;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import simulations.templates.Simulation;
import engine.main.CoreEngine;

public class Start {

	public static void main(String[] args) {
		
		Simulation simulation = new ActionBox();
		GUI gui = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(800, 800, "ActionBox", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();

	}

}
