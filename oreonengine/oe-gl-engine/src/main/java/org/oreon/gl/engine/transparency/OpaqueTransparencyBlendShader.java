package org.oreon.gl.engine.transparency;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class OpaqueTransparencyBlendShader extends GLShaderProgram{
	
private static OpaqueTransparencyBlendShader instance = null;
	
	public static OpaqueTransparencyBlendShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new OpaqueTransparencyBlendShader();
		}
		return instance;
	}
	
	protected OpaqueTransparencyBlendShader() {
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/opaqueTransparencyBlend.comp"));
		
		compileShader();
		
		addUniform("opaqueSceneTexture");
		addUniform("opaqueSceneLightScatteringTexture");
		addUniform("transparencyLayer");
		addUniform("transparencyAlphaMap");
		addUniform("transparencyLayerLightScatteringTexture");
		addUniform("width");
		addUniform("height");
	}
	
	public void updateUniforms(GLTexture opaqueSceneTexture, GLTexture opaqueSceneLightScatteringTexture,
			GLTexture transparencyLayer, GLTexture alphaMap, GLTexture transparencyLayerLightScatteringTexture)
	{
		setUniformf("width", EngineContext.getConfig().getX_ScreenResolution());
		setUniformf("height", EngineContext.getConfig().getY_ScreenResolution());
		
		glActiveTexture(GL_TEXTURE0);
		opaqueSceneTexture.bind();
		setUniformi("opaqueSceneTexture", 0);

		glActiveTexture(GL_TEXTURE2);
		opaqueSceneLightScatteringTexture.bind();
		setUniformi("opaqueSceneLightScatteringTexture", 2);

		glActiveTexture(GL_TEXTURE3);
		transparencyLayer.bind();
		setUniformi("transparencyLayer", 3);

		glActiveTexture(GL_TEXTURE5);
		alphaMap.bind();
		setUniformi("transparencyAlphaMap", 5);
		
		glActiveTexture(GL_TEXTURE6);
		transparencyLayerLightScatteringTexture.bind();
		setUniformi("transparencyLayerLightScatteringTexture", 6);
		
	}

}
