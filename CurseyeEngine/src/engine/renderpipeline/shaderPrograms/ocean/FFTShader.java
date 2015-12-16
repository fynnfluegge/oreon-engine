package engine.renderpipeline.shaderPrograms.ocean;

import engine.core.ResourceLoader;
import engine.renderpipeline.Shader;

public class FFTShader extends Shader{

	private static FFTShader instance = null;
	
	public static FFTShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new FFTShader();
		}
		return instance;
	}
		
	protected FFTShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("ocean/fft/Butterfly.glsl"));
		compileShader();
		
		addUniform("direction");
		addUniform("pingpong");
		addUniform("stage");
	}
		
	public void sendUniforms(int pingpong, int direction, int stage)
	{
		setUniformi("pingpong", pingpong);
		setUniformi("direction", direction);
		setUniformi("stage", stage);
	}
}
