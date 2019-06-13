package org.oreon.gl.components.terrain;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class SplatMapShader extends GLShaderProgram{

	private static SplatMapShader instance = null;

	public static SplatMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SplatMapShader();
	    }
	      return instance;
	}
	
	protected SplatMapShader() {
	
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/SplatMap.comp"));
		compileShader();
		
	}
}
