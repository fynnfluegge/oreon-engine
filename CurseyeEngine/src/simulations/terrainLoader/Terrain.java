package simulations.terrainLoader;

import engine.renderer.terrain.TerrainObject;

public class Terrain extends TerrainObject{

	public Terrain() {
		super(128);
		
		this.loadSettingsFile("./res/terrains/terrain0/terrainSettings.ter");
		setSightRangeFactor(10);
	}

}
