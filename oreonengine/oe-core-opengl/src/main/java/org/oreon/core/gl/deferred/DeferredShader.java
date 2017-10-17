package org.oreon.core.gl.deferred;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/deferred/deferred_CS.glsl"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("LightViewProjections");
	}
	
	public void updateUniforms(GBuffer gbuffer){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
	}
}
