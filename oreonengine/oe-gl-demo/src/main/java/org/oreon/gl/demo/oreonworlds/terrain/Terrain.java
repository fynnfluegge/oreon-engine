package org.oreon.gl.demo.oreonworlds.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.terrain.GLTerrainContext;
import org.oreon.modules.gl.terrain.fractals.FractalMap;

public class Terrain extends GLTerrain{

	public Terrain(GLShader shader, GLShader wireframe, GLShader shadow) {
		super(shader, wireframe, shadow);
	}

	@Override
	public void update(){
		
		super.update();
		
		// create new heightmap from random fractals
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_L)){
			
			List<FractalMap> newFractals = new ArrayList<>();
			
			for (FractalMap fractal : GLTerrainContext.getConfiguration().getFractals()){
				fractal.getHeightmap().delete();
				
				FractalMap newfractal = new FractalMap(Constants.TERRAIN_FRACTALS_RESOLUTION,
													   fractal.getAmplitude(),
													   fractal.getL(),
													   fractal.getScaling(),
													   fractal.getStrength(),
													   new Random().nextInt(1000));
				newFractals.add(newfractal);
			}
			
			// update configurations
			GLTerrainContext.getConfiguration().getFractals().clear();
			for (FractalMap newFracral : newFractals){
				GLTerrainContext.getConfiguration().getFractals().add(newFracral);
			}
			GLTerrainContext.getConfiguration().renderFractalMap();
			GLTerrainContext.getConfiguration().createHeightmapDataBuffer();
		}
	}
}
