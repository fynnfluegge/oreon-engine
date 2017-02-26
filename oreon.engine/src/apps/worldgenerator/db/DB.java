package apps.worldgenerator.db;

import modules.terrain.TerrainConfiguration;

public class DB {
	
	private static TerrainConfiguration terrainConfiguration;
	
	public DB(){
		
	}

	public static TerrainConfiguration getTerrainConfiguration() {
		return terrainConfiguration;
	}

	public static void setTerrainConfiguration(TerrainConfiguration terrainConfiguration) {
		DB.terrainConfiguration = terrainConfiguration;
	}

}
