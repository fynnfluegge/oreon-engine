package org.oreon.gl.components.terrain;

import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class FractalMapShader extends GLShaderProgram{
	
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/FractalMap_CS.glsl"));
		compileShader();
		
		addUniform("N");
		
		for (int i=0; i<8; i++){
			addUniform("fractals[" + i + "].heightmap");
			addUniform("fractals[" + i + "].scaling");
			addUniform("fractals[" + i + "].strength");
		}
	}
	
	public void updateUniforms(List<FractalMap> fractals, int N){
		
		setUniformi("N", N);
		
		for (int i=0; i<8; i++)
		{
			glActiveTexture(GL_TEXTURE0 + i);
			fractals.get(i).getHeightmap().bind();
			setUniformi("fractals[" + i +"].heightmap", i);	
			setUniformi("fractals[" + i +"].scaling", fractals.get(i).getScaling());
			setUniformf("fractals[" + i +"].strength", fractals.get(i).getStrength());
		}
	}

}
