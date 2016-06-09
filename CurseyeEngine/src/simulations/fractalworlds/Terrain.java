package simulations.fractalworlds;

import engine.shaders.terrain.TerrainFractalGrid;
import engine.shaders.terrain.TerrainFractalTessellation;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/terrains/terrain0/terrainSettings.ter", TerrainFractalGrid.getInstance(),
				TerrainFractalTessellation.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(1f);
	}
}
