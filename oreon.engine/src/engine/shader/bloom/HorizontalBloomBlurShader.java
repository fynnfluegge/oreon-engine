package engine.shader.bloom;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class HorizontalBloomBlurShader extends Shader{

	private static HorizontalBloomBlurShader instance = null;
	
	public static HorizontalBloomBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new HorizontalBloomBlurShader();
	    }
	      return instance;
	}
	
	protected HorizontalBloomBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/horizontalGaussianBloom_CS.glsl"));
		
		compileShader();
	}
}
