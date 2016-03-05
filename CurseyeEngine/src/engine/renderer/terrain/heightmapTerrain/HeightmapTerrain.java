package engine.renderer.terrain.heightmapTerrain;

import engine.renderer.terrain.TerrainObject;
import engine.renderer.terrain.fractals.FractalMaps;
import engine.shaderprograms.terrain.specificHeightmap.TerrainGrid;
import engine.shaderprograms.terrain.specificHeightmap.TerrainTessellation;

public class HeightmapTerrain extends TerrainObject{

	public HeightmapTerrain(String file) {
		
		super(file, TerrainGrid.getInstance(),
						TerrainTessellation.getInstance());
		
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,2,100));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,2,100));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,4,100));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,6,100));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,10,400));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,20,1000));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,20,1000));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,2,1000));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,2,1000));
		getTerrainConfiguration().getFractals().add(new FractalMaps(512,2,1000));
	}
}
