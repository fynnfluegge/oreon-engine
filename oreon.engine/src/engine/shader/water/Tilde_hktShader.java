package engine.shader.water;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class Tilde_hktShader extends Shader{

	private static Tilde_hktShader instance = null;
	
	public static Tilde_hktShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Tilde_hktShader();
	    }
	    return instance;
	}
	
	protected Tilde_hktShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/ocean/fft/~h(k,t).glsl"));
		compileShader();
		
		addUniform("L");
		addUniform("t");
	}
	
	public void updateUniforms(int L, float t)
	{
		setUniformi("L", L);
		setUniformf("t", t);
	}
}
