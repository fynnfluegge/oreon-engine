package simulations.objLoader;

import modules.gui.GUI;
import modules.gui.GUIs.EngineGUI;
import simulations.templates.Simulation;
import engine.main.CoreEngine;

public class Start {

	public static void main(String[] args) {
		
		Simulation simulation = new ActionBox();
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(1100, 680, "ActionBox", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();

	}

}
