package org.oreon.modules.gl.terrain;

import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.gl.terrain.fractals.FractalMaps;

public class FractalMapShader extends GLShader{
	
	private static FractalMapShader instance = null;

	public static FractalMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalMapShader();
	    }
	      return instance;
	}
	
	protected FractalMapShader() {
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/FractalMap.glsl"));
		compileShader();
		
		for (int i=0; i<8; i++){
			addUniform("fractals[" + i + "].heightmap");
			addUniform("fractals[" + i + "].scaling");
			addUniform("fractals[" + i + "].strength");
		}
	}
	
	public void updateUniforms(List<FractalMaps> fractals){
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE0 + i);
			fractals.get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", i);	
			setUniformi("fractals0[" + i +"].scaling", fractals.get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", fractals.get(i).getStrength());
		}
	}

}
