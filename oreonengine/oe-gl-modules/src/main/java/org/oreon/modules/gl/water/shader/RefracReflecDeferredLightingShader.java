package org.oreon.modules.gl.water.shader;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class RefracReflecDeferredLightingShader extends GLShader{

	private static RefracReflecDeferredLightingShader instance = null;
	
	public static RefracReflecDeferredLightingShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new RefracReflecDeferredLightingShader();
		}
		return instance;
	}
		
	protected RefracReflecDeferredLightingShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/ocean/RefractionReflectionShader_CS.glsl"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
	}
	
	public void updateUniforms(){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
	}
}
