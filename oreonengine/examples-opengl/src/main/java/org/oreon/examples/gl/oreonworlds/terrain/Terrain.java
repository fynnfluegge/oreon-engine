package org.oreon.examples.gl.oreonworlds.terrain;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.terrain.fractals.FractalMap;

public class Terrain extends GLTerrain{

	public Terrain(GLShaderProgram shader, GLShaderProgram wireframe, GLShaderProgram shadow) {
		super(shader, wireframe, shadow);
	}

	@Override
	public void update(){
		
		super.update();
		
		// create new heightmap from random fractals
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_L)){
			
			List<FractalMap> newFractals = new ArrayList<>();
			
			for (FractalMap fractal : getConfiguration().getFractals()){
				fractal.getHeightmap().delete();
				
				FractalMap newfractal = new FractalMap(fractal.getN(), fractal.getL(),
						fractal.getAmplitude(), fractal.getDirection(), fractal.getIntensity(),
						fractal.getCapillar(), fractal.getScaling(),
						fractal.getHeightStrength(), fractal.getNormalStrength(),
						new Random().nextInt(1000));
				newFractals.add(newfractal);
			}
			
			// update configurations
			getConfiguration().getFractals().clear();
			for (FractalMap newFracral : newFractals){
				getConfiguration().getFractals().add(newFracral);
			}
			memFree(getConfiguration().getHeightmapDataBuffer());
			getConfiguration().renderFractalMap();
			getConfiguration().createHeightmapDataBuffer();
		}
	}
}
