package org.oreon.core.shaders.bloom;

import org.oreon.core.shaders.Shader;
import org.oreon.core.utils.ResourceLoader;

public class BloomShader extends Shader{
	
	private static BloomShader instance = null;
	
	public static BloomShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomShader();
	    }
	      return instance;
	}
	
	protected BloomShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Bloom/Bloom_CS.glsl"));
		
		compileShader();
	}
}
