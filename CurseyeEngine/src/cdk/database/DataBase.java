package cdk.database;

import modules.terrain.TerrainConfiguration;

public class DataBase {
	
	private static TerrainConfiguration terrainConfiguration;
	
	public DataBase(){
		
	}

	public static TerrainConfiguration getTerrainConfiguration() {
		return terrainConfiguration;
	}

	public static void setTerrainConfiguration(TerrainConfiguration terrainConfiguration) {
		DataBase.terrainConfiguration = terrainConfiguration;
	}

}
