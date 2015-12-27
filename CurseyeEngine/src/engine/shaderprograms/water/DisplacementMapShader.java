package engine.shaderprograms.water;

import engine.core.ResourceLoader;
import engine.shaderprograms.Shader;

public class DisplacementMapShader extends Shader{

	private static DisplacementMapShader instance = null;
	
	public static DisplacementMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new DisplacementMapShader();
	    }
	    return instance;
	}
		
	protected DisplacementMapShader()
	{
		super();
			
		addComputeShader(ResourceLoader.loadShader("ocean/fft/OceanDisplacementMap.glsl"));
		compileShader();
			
		addUniform("pingpong");
		addUniform("N");
	}
		
	public void sendUniforms(int N, int pingpong)
	{
		setUniformi("N", N);
		setUniformi("pingpong", pingpong);
	}
}
