package org.oreon.engine.engine.shaders.bloom;

import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/BloomBlurScene_CS.glsl"));
		
		compileShader();
	}
}
