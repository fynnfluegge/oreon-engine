package engine.shaderprograms.water.fft;

import engine.core.ResourceLoader;
import engine.shaderprograms.Shader;

public class PhillipsFourierComponentsShader extends Shader{

	private static PhillipsFourierComponentsShader instance = null;
	
	public static PhillipsFourierComponentsShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PhillipsFourierComponentsShader();
	    }
	    return instance;
	}
	
	protected PhillipsFourierComponentsShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("ocean/fft/PhillipsFourierComponents.glsl"));
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
