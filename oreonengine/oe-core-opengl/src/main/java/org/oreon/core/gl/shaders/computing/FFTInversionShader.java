package org.oreon.core.gl.shaders.computing;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class FFTInversionShader extends GLShader{

	private static FFTInversionShader instance = null;
	
	public static FFTInversionShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FFTInversionShader();
	    }
	    return instance;
	}
		
	protected FFTInversionShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("shaders/computing/FastFourierTransform/Inversion.glsl"));
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
