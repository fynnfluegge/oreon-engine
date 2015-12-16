package engine.renderpipeline.shaderPrograms.ocean;

import engine.core.ResourceLoader;
import engine.math.Vec2f;
import engine.renderpipeline.Shader;

public class PhillipsSpectrumShader extends Shader{

	private static PhillipsSpectrumShader instance = null;
	
	public static PhillipsSpectrumShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PhillipsSpectrumShader();
	    }
	      return instance;
	}
	
	protected PhillipsSpectrumShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("ocean/fft/PhillipsSpectrum.glsl"));
		compileShader();
		
		addUniform("N");
		addUniform("L");
		addUniform("A");
		addUniform("w");
		addUniform("l");
		addUniform("noise_r0");
		addUniform("noise_i0");
		addUniform("noise_r1");
		addUniform("noise_i1");
	}
	
	public void sendUniforms(int N, int L, float A, Vec2f w, float l)
	{
		setUniformi("N", N);
		setUniformi("L", L);
		setUniformf("A", A);
		setUniformf("l", l);
		setUniform("w", w);
	}
	
	public void sendUniforms(int texture0, int texture1, int texture2, int texture3)
	{
		setUniformi("noise_r0", texture0);
		setUniformi("noise_i0", texture1);
		setUniformi("noise_r1", texture2);
		setUniformi("noise_i1", texture3);
	}
}
