package simulations.worldLoader;

import modules.terrain.TerrainObject;
import engine.shaders.terrain.TerrainGrid;
import engine.shaders.terrain.TerrainTessellation;

public class Terrain extends TerrainObject{

	public Terrain() {
		
		
		
		super("./res/terrains/terrain1/terrainSettings.ter", TerrainGrid.getInstance(),
				TerrainTessellation.getInstance());
		
		this.getTerrainConfiguration().setSightRangeFactor(3);
	}

}
