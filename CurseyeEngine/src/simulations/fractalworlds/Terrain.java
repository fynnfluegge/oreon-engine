package simulations.fractalworlds;

import modules.terrain.TerrainObject;
import engine.shaders.terrain.TerrainFractalGrid;
import engine.shaders.terrain.TerrainFractalTessellation;

public class Terrain extends TerrainObject{

	public Terrain() {
		
		super("./res/terrains/terrain0/terrainSettings.ter", TerrainFractalGrid.getInstance(),
				TerrainFractalTessellation.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(3);
	}
}
