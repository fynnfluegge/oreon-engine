package simulations.oceanSurface.oceanSimulation;

import modules.gui.GUI;
import modules.sky.SkySphere;
import engine.main.CoreEngine;
import engine.main.Simulation;

public class OceanSimulation extends Simulation{

	public static void main(String[] args) {
		Simulation simulation = new OceanSimulation();
		GUI gui = new FFTMapGUI();
		CoreEngine coreEngine = new CoreEngine(1000, 600, "Ocean Simulation");
		coreEngine.createWindow();
		coreEngine.init(simulation, gui);
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		scenegraph.addObject(new SkySphere());
		scenegraph.setWater(new Ocean());
	}
}
