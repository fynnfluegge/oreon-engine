package org.oreon.gl.components.fft;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class ButterflyShader extends GLShaderProgram{

	private static ButterflyShader instance = null;
	
	public static ButterflyShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new ButterflyShader();
		}
		return instance;
	}
		
	protected ButterflyShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("shaders/fft/butterfly.comp"));
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
