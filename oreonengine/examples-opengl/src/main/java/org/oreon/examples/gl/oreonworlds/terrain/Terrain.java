package org.oreon.examples.gl.oreonworlds.terrain;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.gl.components.terrain.FractalMap;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.terrain.TerrainConfiguration;

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
			
			for (FractalMap fractal : GLContext.getObject(TerrainConfiguration.class).getFractals()){
				fractal.getHeightmap().delete();
				
				FractalMap newfractal = new FractalMap(fractal.getN(), fractal.getL(),
						fractal.getAmplitude(), fractal.getDirection(), fractal.getIntensity(),
						fractal.getCapillarSuppression(), fractal.getScaling(), fractal.getStrength(),
						new Random().nextInt(1000));
				newFractals.add(newfractal);
			}
			
			// update configurations
			GLContext.getObject(TerrainConfiguration.class).getFractals().clear();
			for (FractalMap newFracral : newFractals){
				GLContext.getObject(TerrainConfiguration.class).getFractals().add(newFracral);
			}
			memFree(GLContext.getObject(TerrainConfiguration.class).getHeightmapDataBuffer());
			GLContext.getObject(TerrainConfiguration.class).renderFractalMap();
			GLContext.getObject(TerrainConfiguration.class).createHeightmapDataBuffer();
		}
	}
}
