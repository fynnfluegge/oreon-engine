package engine.shader.bloom;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class BloomBlurShader extends Shader{

	private static BloomBlurShader instance = null;
	
	public static BloomBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomBlurShader();
	    }
	      return instance;
	}
	
	protected BloomBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/BloomBlur_CS.glsl"));
		
		compileShader();
	}
}
