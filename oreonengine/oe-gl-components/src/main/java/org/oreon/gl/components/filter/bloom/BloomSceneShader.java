package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class BloomSceneShader extends GLShaderProgram{

	private static BloomSceneShader instance = null;
	
	public static BloomSceneShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomSceneShader();
	    }
	      return instance;
	}
	
	protected BloomSceneShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloomScene.comp"));
		
		compileShader();
	}
}
