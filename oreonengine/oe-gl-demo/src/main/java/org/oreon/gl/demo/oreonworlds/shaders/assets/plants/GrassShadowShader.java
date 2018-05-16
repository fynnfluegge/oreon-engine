package org.oreon.gl.demo.oreonworlds.shaders.assets.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class GrassShadowShader extends GLShaderProgram{

	private static GrassShadowShader instance;

	public static GrassShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GrassShadowShader();
	    }
	     return instance;
	}
	
	protected GrassShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_FS.glsl"));
		compileShader();
		
		addUniformBlock("worldMatrices");
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
		addUniform("material.diffusemap");
		
		for (int i=0; i<500; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(Renderable object){
		
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		((GLInstancedCluster) object.getParentNode()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		
		@SuppressWarnings("unchecked")
		Material<GLTexture> material = (Material<GLTexture>) object.getComponent(NodeComponentType.MATERIAL0);

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((InstancedCluster) object.getParentNode()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}

}
