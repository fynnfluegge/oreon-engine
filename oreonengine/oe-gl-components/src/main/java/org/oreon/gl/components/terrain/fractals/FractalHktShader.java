package org.oreon.gl.components.terrain.fractals;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class FractalHktShader extends GLShaderProgram{
	
	private static FractalHktShader instance = null;
	
	public static FractalHktShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalHktShader();
	    }
	    return instance;
	}
	
	protected FractalHktShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/fractalHkt.comp"));
		compileShader();
		
		addUniform("L");
		addUniform("t");
	}
	
	public void updateUniforms(int L, int N, float t)
	{
		setUniformi("L", L);
		setUniformf("t", t);
	}

}
