package org.oreon.demo.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.Constants;
import org.oreon.core.utils.ResourceLoader;
import org.oreon.demo.oreonworlds.assets.plants.Palm01Cluster;
import org.oreon.modules.terrain.Terrain;
import org.oreon.system.desktop.GLRenderingEngine;

public class PalmShader extends Shader{

	private static PalmShader instance = null;
	
	public static PalmShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmShader();
	    }
	      return instance;
	}
	
	protected PalmShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.color");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
//		addUniform("material.emission");
//		addUniform("material.shininess");
		
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
		setUniform("material.color", material.getColor());
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
		
		glActiveTexture(GL_TEXTURE1);
		GLRenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 1);
		
		List<Integer> indices = ((Palm01Cluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
