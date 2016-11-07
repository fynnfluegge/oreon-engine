package engine.shaders.terrain.fractals;

import engine.core.ResourceLoader;
import engine.shaders.Shader;

public class FractalFourierComponentsShader extends Shader{

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
		
		addUniform("L");
		addUniform("t");
	}
	
	public void updateUniforms(int L, float t)
	{
		setUniformi("L", L);
		setUniformf("t", t);
	}
}
