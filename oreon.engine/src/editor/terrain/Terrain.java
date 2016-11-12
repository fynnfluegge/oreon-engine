package editor.terrain;

import editor.db.DB;

public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/editor/terrainEditor/terrainSettings.ter", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(2f);
		
		DB.setTerrainConfiguration(getTerrainConfiguration());
	}
}