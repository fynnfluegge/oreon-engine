package org.oreon.gl.components.terrain.fractals;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class FractalFFTInversionShader extends GLShaderProgram{

private static FractalFFTInversionShader instance = null;
	
	public static FractalFFTInversionShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalFFTInversionShader();
	    }
	    return instance;
	}
		
	protected FractalFFTInversionShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/fractalFFTInversion.comp"));
		compileShader();
			
		addUniform("pingpong");
		addUniform("N");
	}
		
	public void updateUniforms(int N, int pingpong)
	{
		setUniformi("N", N);
		setUniformi("pingpong", pingpong);
	}
}
