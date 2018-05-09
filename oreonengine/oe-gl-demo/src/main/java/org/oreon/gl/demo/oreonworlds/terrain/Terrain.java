package org.oreon.gl.demo.oreonworlds.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.gl.components.terrain.FractalMap;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.terrain.GLTerrainContext;

public class Terrain extends GLTerrain{

	public Terrain(GLShaderProgram shader, GLShaderProgram wireframe, GLShaderProgram shadow) {
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
				
				FractalMap newfractal = new FractalMap(fractal.getN(), fractal.getL(),
						fractal.getAmplitude(), fractal.getDirection(), fractal.getIntensity(),
						fractal.getCapillarSuppression(), fractal.getScaling(), fractal.getStrength(),
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
