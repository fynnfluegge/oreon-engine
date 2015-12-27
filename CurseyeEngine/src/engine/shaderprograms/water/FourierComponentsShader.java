package engine.shaderprograms.water;

import engine.core.ResourceLoader;
import engine.shaderprograms.Shader;

public class FourierComponentsShader extends Shader{

	private static FourierComponentsShader instance = null;
	
	public static FourierComponentsShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FourierComponentsShader();
	    }
	    return instance;
	}
	
	protected FourierComponentsShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("ocean/fft/FourierComponents.glsl"));
		compileShader();
		
		addUniform("L");
		addUniform("N");
		addUniform("t");
	}
	
	public void sendUniforms(int L, int N, float t)
	{
		setUniformi("L", L);
		setUniformi("N", N);
		setUniformf("t", t);
	}
}
