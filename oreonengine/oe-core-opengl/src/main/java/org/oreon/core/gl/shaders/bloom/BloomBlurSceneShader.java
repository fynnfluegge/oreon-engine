package org.oreon.core.gl.shaders.bloom;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class BloomBlurSceneShader extends GLShader{

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
