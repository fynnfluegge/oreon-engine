package simulations.oceanSurface.oceanSimulation;

import modules.lighting.DirectionalLight;
import engine.gui.GUI;
import engine.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import simulations.templates.Simulation;
import simulations.templates.TerrainSimulation;

public class OceanSimulation extends TerrainSimulation{

	public static void main(String[] args) {
		Simulation simulation = new OceanSimulation();
		GUI gui = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(1600, 900, "Ocean Simulation", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		setWater(new Ocean());
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(-4,-2,-1).normalize(), new Vec3f(0.02f,0.02f,0.02f), new Vec3f(1.0f, 0.95f, 0.87f), 2.0f));
	}
}
