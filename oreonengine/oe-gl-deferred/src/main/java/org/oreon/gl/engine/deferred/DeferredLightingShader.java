package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class DeferredLightingShader extends GLShaderProgram{

	private static DeferredLightingShader instance = null;
	
	public static DeferredLightingShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new DeferredLightingShader();
		}
		return instance;
	}
		
	protected DeferredLightingShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/deferredLighting.comp"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("LightViewProjections");
		addUniform("numSamples");
		addUniform("pssm");
//		addUniform("sightRangeFactor");
		addUniform("ssaoFlag");
//		addUniform("fogColor");
	}
	
	public void updateUniforms(GLTexture pssm, boolean ssaoFlag){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
//		setUniformf("sightRangeFactor", BaseContext.getConfig().getSightRange());
//		setUniform("fogColor", BaseContext.getConfig().getFogColor());
		
		glActiveTexture(GL_TEXTURE1);
		pssm.bind();
		setUniformi("pssm", 1);
		
		setUniformi("ssaoFlag", ssaoFlag ? 1 : 0);
		
		setUniformi("numSamples", BaseContext.getConfig().getMultisamples());
	}
}
