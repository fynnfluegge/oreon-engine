package engine.shaderprograms.fft;

import engine.core.ResourceLoader;
import engine.shaderprograms.Shader;

public class InversionShader extends Shader{

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
			
		addComputeShader(ResourceLoader.loadShader("gpgpu/FastFourierTransform/Inversion.glsl"));
		compileShader();
			
		addUniform("pingpong");
		addUniform("N");
	}
		
	public void sendUniforms(int N, int pingpong)
	{
		setUniformi("N", N);
		setUniformi("pingpong", pingpong);
	}
}
