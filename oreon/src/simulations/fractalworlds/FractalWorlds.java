package simulations.fractalworlds;

import modules.sky.SkySphere;
import engine.main.CoreEngine;
import engine.main.Simulation;

public class FractalWorlds extends Simulation{
	
	public static void main(String[] args) {
		Simulation simulation = new FractalWorlds();
		GUI gui = new GUI();
		CoreEngine coreEngine = new CoreEngine(800, 600, "FractalWorlds");
		coreEngine.createWindow();
		coreEngine.init(simulation, gui);
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		scenegraph.setTerrain(new Terrain());
		scenegraph.addObject(new SkySphere());
	}
}
