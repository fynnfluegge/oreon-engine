package cdk.tools.terrainEditor;

import cdk.database.DataBase;
import engine.shaders.terrain.TerrainGrid;
import engine.shaders.terrain.TerrainTessellation;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/terrains/terrain0/terrainSettings.ter", TerrainGrid.getInstance(),
				TerrainTessellation.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(2f);
		
		DataBase.setTerrainConfiguration(getTerrainConfiguration());
	}
}