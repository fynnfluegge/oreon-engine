package org.oreon.core.gl.deferred;

import org.oreon.core.gl.shaders.GLShader;

public class DeferredShader extends GLShader{

private static DeferredShader instance = null;
	
	public static DeferredShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new DeferredShader();
		}
		return instance;
	}
		
	protected DeferredShader()
	{
		super();
	}
	
	public void updateUniforms(){
		
	}
}
