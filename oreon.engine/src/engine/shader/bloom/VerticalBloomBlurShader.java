package engine.shader.bloom;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class VerticalBloomBlurShader extends Shader{

	private static VerticalBloomBlurShader instance = null;
	
	public static VerticalBloomBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new VerticalBloomBlurShader();
	    }
	      return instance;
	}
	
	protected VerticalBloomBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/verticalGaussianBloom_CS.glsl"));
		
		compileShader();
	}
}
