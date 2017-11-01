package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
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
		addUniform("transparencyLayer");
//		addUniform("opaqueSceneDepthMap");
//		addUniform("transparencyLayerDepthMap");
		addUniform("alphaMap");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(Texture opaqueSceneTexture, Texture opaqueSceneDepthMap,
							   Texture transparencyLayer, Texture transparencyLayerDepthMap,
							   Texture alphaMap)
	{
		glActiveTexture(GL_TEXTURE0);
		opaqueSceneTexture.bind();
		setUniformi("opaqueSceneTexture", 0);
		
//		glActiveTexture(GL_TEXTURE1);
//		opaqueSceneDepthMap.bind();
//		setUniformi("opaqueSceneDepthMap", 1);

		glActiveTexture(GL_TEXTURE2);
		transparencyLayer.bind();
		setUniformi("transparencyLayer", 2);
		
//		glActiveTexture(GL_TEXTURE3);
//		transparencyLayerDepthMap.bind();
//		setUniformi("transparencyLayerDepthMap", 3);

		glActiveTexture(GL_TEXTURE4);
		alphaMap.bind();
		setUniformi("alphaMap", 4);
	}
}
