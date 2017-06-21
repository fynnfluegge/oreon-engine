package org.oreon.engine.engine.shaders.computing;

import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

public class FFTInversionShader extends Shader{

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
			
		addComputeShader(ResourceLoader.loadShader("shaders/computing/fastFourierTransform/Inversion.glsl"));
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
