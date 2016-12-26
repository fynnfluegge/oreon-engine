package engine.shader.terrain.fractals;

import engine.math.Vec2f;
import engine.shader.Shader;
import engine.utils.ResourceLoader;


public class FractalSpectrumShader extends Shader{

private static FractalSpectrumShader instance = null;
	
	public static FractalSpectrumShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalSpectrumShader();
	    }
	      return instance;
	}
	
	protected FractalSpectrumShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/fractals/FractalSpectrum.glsl"));
		compileShader();
		
		addUniform("N");
		addUniform("L");
		addUniform("A");
		addUniform("w");
		addUniform("l");
		addUniform("V");
		addUniform("noise_r0");
		addUniform("noise_i0");
		addUniform("noise_r1");
		addUniform("noise_i1");
	}
	
	public void updateUniforms(int N, int L, float A, Vec2f w, float v, float l)
	{
		setUniformi("N", N);
		setUniformi("L", L);
		setUniformf("A", A);
		setUniformf("l", l);
		setUniform("w", w);
		setUniformf("V", v);
	}
	
	public void updateUniforms(int texture0, int texture1, int texture2, int texture3)
	{
		setUniformi("noise_r0", texture0);
		setUniformi("noise_i0", texture1);
		setUniformi("noise_r1", texture2);
		setUniformi("noise_i1", texture3);
	}
}
