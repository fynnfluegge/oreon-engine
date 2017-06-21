package org.oreon.engine.engine.shaders.computing;

import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

public class FFTButterflyShader extends Shader{

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
			
		addComputeShader(ResourceLoader.loadShader("shaders/computing/fastFourierTransform/Butterfly.glsl"));
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
