package org.oreon.demo.gl.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.terrain.Terrain;
import org.oreon.system.gl.desktop.GLRenderingEngine;

public class TreeTrunkShader extends GLShader{

	private static TreeTrunkShader instance = null;

	public static TreeTrunkShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TreeTrunkShader();
	    }
	      return instance;
	}
	
	protected TreeTrunkShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Tree_Shader/TreeTrunk_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Tree_Shader/TreeTrunk_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Tree_Shader/TreeTrunk_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("material.normalmap");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
		addUniform("shadowMaps");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}	
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformi("isReflection", CoreSystem.getInstance().getRenderingEngine().isWaterReflection() ? 1 : 0);
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		setUniformf("sightRangeFactor", Terrain.getInstance().getConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material.normalmap", 1);
		
		glActiveTexture(GL_TEXTURE2);
		GLRenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 2);
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
