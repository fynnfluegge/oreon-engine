package editor.terrain;

import editor.db.DB;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/cdk/terrainEditor/terrainSettings.ter", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(2f);
		
		DB.setTerrainConfiguration(getTerrainConfiguration());
	}
}