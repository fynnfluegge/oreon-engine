package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.texture.Texture2DMultisample;
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
		addUniform("depthmap");
		addUniform("multisamples");
	}
	
	public void updateUniforms(Texture2DMultisample depthmapMS){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		glActiveTexture(GL_TEXTURE0);
		depthmapMS.bind();
		setUniformi("depthmap", 0);
		setUniformi("multisamples", Constants.MULTISAMPLES);
	}
}
