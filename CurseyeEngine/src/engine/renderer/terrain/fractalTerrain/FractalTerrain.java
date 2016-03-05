package engine.renderer.terrain.fractalTerrain;

import engine.renderer.terrain.TerrainObject;
import engine.renderer.terrain.fractals.FractalMaps;
import engine.shaderprograms.terrain.fractal.TerrainFractalGrid;
import engine.shaderprograms.terrain.fractal.TerrainFractalTessellation;

public class FractalTerrain extends TerrainObject{

	public FractalTerrain(String file) {
		
		super(file, TerrainFractalGrid.getInstance(),
						TerrainFractalTessellation.getInstance());

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
