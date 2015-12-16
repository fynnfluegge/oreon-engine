package simulations.terrain;

import engine.gui.GUI;
import engine.gui.GUIs.EngineGUI;
import engine.main.CoreEngine;
import simulations.templates.Simulation;

public class Start{

	
public static void main(String[] args) {
		
		Simulation simulation = new TerrainEditor();
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(1080, 640, "Terrain", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();
	}	
}
