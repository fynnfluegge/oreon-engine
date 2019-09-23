package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/deferredLighting.comp", "lib.glsl"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("DirectionalLightViewProjections");
		addUniform("numSamples");
		addUniform("pssm");
		addUniform("sightRangeFactor");
		addUniform("fogColor");
		addUniform("shadowsEnable");
		addUniform("shadowsQuality");
		addUniform("ssaoEnable");
	}
	
	public void updateUniforms(GLTexture pssm){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("DirectionalLightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformf("sightRangeFactor", BaseContext.getConfig().getSightRange());
		setUniform("fogColor", BaseContext.getConfig().getFogColor());
		setUniformi("shadowsEnable", BaseContext.getConfig().isShadowsEnable() ? 1 : 0);
		setUniformi("shadowsQuality", BaseContext.getConfig().getShadowsQuality());
		
		if (BaseContext.getConfig().isShadowsEnable()) {
			glActiveTexture(GL_TEXTURE0);
			pssm.bind();
			setUniformi("pssm", 0);
		}
		
		setUniformi("ssaoEnable", BaseContext.getConfig().isSsaoEnabled() ? 1 : 0);
		setUniformi("numSamples", BaseContext.getConfig().getMultisampling_sampleCount());
	}
}
