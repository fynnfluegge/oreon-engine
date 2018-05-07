package org.oreon.gl.components.fft;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class InversionShader extends GLShaderProgram{

	private static InversionShader instance = null;
	
	public static InversionShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new InversionShader();
	    }
	    return instance;
	}
		
	protected InversionShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("shaders/fft/inversion.comp"));
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
