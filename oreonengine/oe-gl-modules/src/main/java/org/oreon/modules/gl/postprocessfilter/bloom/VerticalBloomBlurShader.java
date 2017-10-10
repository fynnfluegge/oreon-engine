package org.oreon.modules.gl.postprocessfilter.bloom;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class VerticalBloomBlurShader extends GLShader{

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Bloom/verticalGaussianBloom_CS.glsl"));
		
		compileShader();
	}
}
