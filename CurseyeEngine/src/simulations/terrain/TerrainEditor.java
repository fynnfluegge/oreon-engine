package simulations.terrain;

import engine.lighting.DirectionalLight;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import engine.renderer.water.Ocean;
import simulations.templates.TerrainSimulation;

public class TerrainEditor extends TerrainSimulation{

	public void init()
	{	
		super.init();
		setWater(new Ocean());
		SwingGUI.setSimulation(this);
		SwingGUI.start();
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(0,-1,0), new Vec3f(1.0f,1.0f,1.0f), new Vec3f(1.0f, 0.95f, 0.75f), 0.9f));
	}
}
