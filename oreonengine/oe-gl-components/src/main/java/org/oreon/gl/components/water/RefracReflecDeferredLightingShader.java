package org.oreon.gl.components.water;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class RefracReflecDeferredLightingShader extends GLShaderProgram{

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
