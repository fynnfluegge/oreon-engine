package simulations.worldLoader;

import engine.renderer.terrain.heightmapTerrain.HeightmapTerrain;

public class Terrain extends HeightmapTerrain{

	public Terrain() {
		super("./res/terrains/terrain0/terrainSettings.ter");
		
		this.getTerrainConfiguration().setSightRangeFactor(10);
	}

}
