package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class BloomShader extends GLShaderProgram{
	
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/Bloom_CS.glsl"));
		
		compileShader();
	}
}
