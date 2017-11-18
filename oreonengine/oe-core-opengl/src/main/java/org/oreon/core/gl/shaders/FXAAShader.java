package org.oreon.core.gl.shaders;

import org.oreon.core.util.ResourceLoader;

public class FXAAShader extends GLShader{

	private static FXAAShader instance = null;
	
	public static FXAAShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new FXAAShader();
		}
		return instance;
	}
		
	protected FXAAShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/anti-aliasing/fxaa_CS.glsl"));
		
		compileShader();
	}
}
