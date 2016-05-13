package engine.shaders.water;

import engine.core.ResourceLoader;
import engine.shaders.Shader;

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
		
		addComputeShader(ResourceLoader.loadShader("ocean/fft/~h(k,t).glsl"));
		compileShader();
		
		addUniform("L");
		addUniform("t");
	}
	
	public void sendUniforms(int L, int N, float t)
	{
		setUniformi("L", L);
		setUniformf("t", t);
	}
}
