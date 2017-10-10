package org.oreon.modules.gl.gpgpu.fft;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class FFTButterflyShader extends GLShader{

	private static FFTButterflyShader instance = null;
	
	public static FFTButterflyShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new FFTButterflyShader();
		}
		return instance;
	}
		
	protected FFTButterflyShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("shaders/computing/FastFourierTransform/Butterfly.glsl"));
		compileShader();
		
		addUniform("direction");
		addUniform("pingpong");
		addUniform("stage");
	}
		
	public void updateUniforms(int pingpong, int direction, int stage)
	{
		setUniformi("pingpong", pingpong);
		setUniformi("direction", direction);
		setUniformi("stage", stage);
	}
}
