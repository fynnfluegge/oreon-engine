package simulations.various;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.main.Simulation;

public class Start {

public static void main(String[] args) {
		
		GUI gui = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(800, 600, "");
		coreEngine.createWindow();
		Simulation simulation = new PSSM();
		coreEngine.init(simulation, gui);
		coreEngine.start();
	}
}
