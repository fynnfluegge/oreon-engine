package org.oreon.gl.demo.oreonworlds.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.terrain.fractals.FractalMap;

public class Terrain extends GLTerrain{

	@Override
	public void update(){
		
		super.update();
		
		// create new heightmap from random fractals
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_L)){
			
			List<FractalMap> newFractals = new ArrayList<>();
			
			for (FractalMap fractal : getConfiguration().getFractals()){
				fractal.getHeightmap().delete();
				
				FractalMap newfractal = new FractalMap(Constants.TERRAIN_FRACTALS_RESOLUTION,
													   fractal.getAmplitude(),
													   fractal.getL(),
													   fractal.getScaling(),
													   fractal.getStrength(),
													   new Random().nextInt(1000));
				newFractals.add(newfractal);
			}
			
			getConfiguration().getFractals().clear();
			
			for (FractalMap newFracral : newFractals){
				getConfiguration().getFractals().add(newFracral);
			}
			
			getConfiguration().renderFractalMap();
			getConfiguration().createHeightmapDataBuffer();
		}
	}
}
