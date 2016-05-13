package simulations.worldLoader;

import modules.lighting.DirectionalLight;
import engine.gui.GUI;
import engine.gui.GUIs.EngineGUI;
import engine.main.CoreEngine;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import simulations.templates.Simulation;
import simulations.templates.TerrainSimulation;

public class WorldLoader extends TerrainSimulation{
	
	public static void main(String[] args) {
		Simulation simulation = new WorldLoader();
		GUI gui = new EngineGUI();
		CoreEngine coreEngine = new CoreEngine(1400, 900, "TerrainLoader", simulation, gui);
		coreEngine.createWindow();
		coreEngine.start();
	}

	public void init()
	{	
		super.init();
//		setTerrain(new Terrain());
		setWater(new Ocean());
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(-4,-2,-1).normalize(), new Vec3f(0.02f,0.02f,0.02f), new Vec3f(1.0f, 0.95f, 0.87f), 2.0f));
	}
}
