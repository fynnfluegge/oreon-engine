package org.oreon.modules.gl.terrain.fractals;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class FractalFourierComponentsShader extends GLShader{

private static FractalFourierComponentsShader instance = null;
	
	public static FractalFourierComponentsShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalFourierComponentsShader();
	    }
	    return instance;
	}
	
	protected FractalFourierComponentsShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/fractals/FractalFourierComponents.glsl"));
		compileShader();
		
		addUniform("N");
		addUniform("L");
		addUniform("t");
	}
	
	public void updateUniforms(int N, int L, float t)
	{
		setUniformi("N", N);
		setUniformi("L", L);
		setUniformf("t", t);
	}
}
