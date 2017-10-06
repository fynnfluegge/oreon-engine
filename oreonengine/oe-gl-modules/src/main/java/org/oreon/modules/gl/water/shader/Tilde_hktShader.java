package org.oreon.modules.gl.water.shader;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class Tilde_hktShader extends GLShader{

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
		
		addUniform("N");
		addUniform("L");
		addUniform("t");
	}
	
	public void updateUniforms(int L, int N, float t)
	{
		setUniformi("N", N);
		setUniformi("L", L);
		setUniformf("t", t);
	}
}
