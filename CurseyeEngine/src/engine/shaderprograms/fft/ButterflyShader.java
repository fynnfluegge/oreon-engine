package engine.shaderprograms.fft;

import engine.core.ResourceLoader;
import engine.shaderprograms.Shader;

public class ButterflyShader extends Shader{

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
			
		addComputeShader(ResourceLoader.loadShader("gpgpu/FastFourierTransform/Butterfly.glsl"));
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
