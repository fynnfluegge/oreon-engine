package engine.shaders.gpcgpu.fft;

import engine.core.ResourceLoader;
import engine.shaders.Shader;

public class TwiddleFactorsShader extends Shader{

	private static TwiddleFactorsShader instance = null;
	
	public static TwiddleFactorsShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TwiddleFactorsShader();
	    }
	      return instance;
	}
	
	protected TwiddleFactorsShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("gpgpu/FastFourierTransform/TwiddleFactors.glsl"));
		compileShader();
		
		addUniform("N");
	}
	

	public void sendUniforms(int N)
	{
		setUniformi("N", N);
	}
}
