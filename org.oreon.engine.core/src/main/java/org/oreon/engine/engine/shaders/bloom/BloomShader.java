package org.oreon.engine.engine.shaders.bloom;

import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/bloom_CS.glsl"));
		
		compileShader();
	}
}
