package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.ResourceLoader;

public class TransparencyBlendShader extends GLShader{

	private static TransparencyBlendShader instance = null;
	
	public static TransparencyBlendShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new TransparencyBlendShader();
		}
		return instance;
	}
	
	protected TransparencyBlendShader() {
		
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/deferred/transparencyBlend_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/deferred/transparencyBlend_FS.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("opaqueSceneTexture");
		addUniform("opaqueSceneLightScatteringTexture");
		addUniform("opaqueSceneDepthMap");
		addUniform("transparencyLayer");
		addUniform("transparencyLayerDepthMap");
		addUniform("transparencyAlphaMap");
		addUniform("transparencyLayerLightScatteringTexture");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(Texture opaqueSceneTexture, Texture opaqueSceneDepthMap,
							   Texture opaqueSceneLightScatteringTexture,
							   Texture transparencyLayer, Texture transparencyLayerDepthMap,
							   Texture alphaMap, Texture transparencyLayerLightScatteringTexture)
	{
		glActiveTexture(GL_TEXTURE0);
		opaqueSceneTexture.bind();
		setUniformi("opaqueSceneTexture", 0);
		
		glActiveTexture(GL_TEXTURE1);
		opaqueSceneDepthMap.bind();
		setUniformi("opaqueSceneDepthMap", 1);
		
		glActiveTexture(GL_TEXTURE2);
		opaqueSceneLightScatteringTexture.bind();
		setUniformi("opaqueSceneLightScatteringTexture", 2);

		glActiveTexture(GL_TEXTURE3);
		transparencyLayer.bind();
		setUniformi("transparencyLayer", 3);
		
		glActiveTexture(GL_TEXTURE4);
		transparencyLayerDepthMap.bind();
		setUniformi("transparencyLayerDepthMap", 4);

		glActiveTexture(GL_TEXTURE5);
		alphaMap.bind();
		setUniformi("transparencyAlphaMap", 5);
		
		glActiveTexture(GL_TEXTURE6);
		transparencyLayerLightScatteringTexture.bind();
		setUniformi("transparencyLayerLightScatteringTexture", 6);
	}
}
