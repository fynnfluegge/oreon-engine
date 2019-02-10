package org.oreon.gl.components.terrain.fractals;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/FractalMap.comp"));
		compileShader();
		
		addUniform("N");
		addUniform("isHeightmap");
		
		for (int i=0; i<8; i++){
			addUniform("fractals[" + i + "].map");
			addUniform("fractals[" + i + "].scaling");
			addUniform("fractals[" + i + "].strength");
		}
	}
	
	public void updateUniforms(List<FractalMap> fractals, int N, boolean renderHeight){
		
		setUniformi("N", N);
		
		for (int i=0; i<8; i++)
		{
			glActiveTexture(GL_TEXTURE0 + i);
			
			if (renderHeight)
				fractals.get(i).getHeightmap().bind();
			else
				fractals.get(i).getNormalmap().bind();
			
			setUniformi("fractals[" + i +"].map", i);	
			setUniformi("fractals[" + i +"].scaling", fractals.get(i).getScaling());
			
			if (renderHeight){
				setUniformi("isHeightmap", 1);
				setUniformf("fractals[" + i +"].strength", fractals.get(i).getHeightStrength());
			}
			else{
				setUniformf("fractals[" + i +"].strength", fractals.get(i).getNormalStrength());
				setUniformi("isHeightmap", 0);
			}
			
		}
	}

}
