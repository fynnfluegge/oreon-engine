package simulations.fractalworlds;


public class Terrain extends modules.terrain.Terrain{

	public Terrain() {
		
		super("./res/demos/FractalWorlds/terrainSettings.ter",
					TerrainShader.getInstance(),
					TerrainGridShader.getInstance(),
					TerrainShadowShader.getInstance());
		
		getTerrainConfiguration().setSightRangeFactor(2f);
	}
}
