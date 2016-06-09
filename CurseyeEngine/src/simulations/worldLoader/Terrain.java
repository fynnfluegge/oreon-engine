package simulations.worldLoader;

import engine.shaders.terrain.TerrainGrid;
import engine.shaders.terrain.TerrainTessellation;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		
		
		super("./res/terrains/terrain1/terrainSettings.ter", TerrainGrid.getInstance(),
				TerrainTessellation.getInstance());
		
		this.getTerrainConfiguration().setSightRangeFactor(1.4f);
	}

}
