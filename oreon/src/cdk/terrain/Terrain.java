package cdk.terrain;

import cdk.database.DataBase;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/cdk/terrainEditor/terrainSettings.ter", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(2f);
		
		DataBase.setTerrainConfiguration(getTerrainConfiguration());
	}
}