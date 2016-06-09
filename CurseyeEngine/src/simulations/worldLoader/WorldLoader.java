package simulations.worldLoader;

import modules.gui.GUI;
import modules.gui.GUIs.EngineGUI;
import modules.lighting.DirectionalLight;
import engine.main.CoreEngine;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import simulations.templates.Simulation;
import simulations.templates.TerrainSimulation;

public class WorldLoader extends TerrainSimulation{
	
	public static void main(String[] args) {
		Simulation simulation = new WorldLoader();
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(800, 800, "TerrainLoader", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
		setTerrain(new Terrain());
//		setWater(new Ocean());
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(-8,-2,-1).normalize(), new Vec3f(0.02f,0.02f,0.02f), new Vec3f(1.0f, 0.95f, 0.87f), 2.0f));
	}
}
