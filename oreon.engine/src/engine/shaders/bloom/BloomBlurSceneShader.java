package engine.shaders.bloom;

import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class BloomBlurSceneShader extends Shader{

	private static BloomBlurSceneShader instance = null;
	
	public static BloomBlurSceneShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomBlurSceneShader();
	    }
	      return instance;
	}
	
	protected BloomBlurSceneShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Bloom/BloomBlurScene_CS.glsl"));
		
		compileShader();
	}
}
