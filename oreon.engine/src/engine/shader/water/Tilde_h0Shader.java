package engine.shader.water;

import engine.math.Vec2f;
import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class Tilde_h0Shader extends Shader{

	private static Tilde_h0Shader instance = null;
	
	public static Tilde_h0Shader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Tilde_h0Shader();
	    }
	      return instance;
	}
	
	protected Tilde_h0Shader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/ocean/fft/~h0.glsl"));
		compileShader();
		
		addUniform("N");
		addUniform("L");
		addUniform("A");
		addUniform("w");
		addUniform("windspeed");
		addUniform("noise_r0");
		addUniform("noise_i0");
		addUniform("noise_r1");
		addUniform("noise_i1");
	}
	
	public void updateUniforms(int N, int L, float A, Vec2f w, float windspeed)
	{
		setUniformi("N", N);
		setUniformi("L", L);
		setUniformf("A", A);
		setUniformf("windspeed", windspeed);
		setUniform("w", w);
	}
	
	public void updateUniforms(int texture0, int texture1, int texture2, int texture3)
	{
		setUniformi("noise_r0", texture0);
		setUniformi("noise_i0", texture1);
		setUniformi("noise_r1", texture2);
		setUniformi("noise_i1", texture3);
	}
}
