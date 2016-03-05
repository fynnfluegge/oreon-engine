package simulations.fractalworlds;

import engine.renderer.terrain.fractalTerrain.FractalTerrain;

public class Terrain extends FractalTerrain{

	public Terrain() {
		super("./res/terrains/terrain0/terrainSettings.ter");
		
		getTerrainConfiguration().setSightRangeFactor(1.4f);
	}
}
