package org.oreon.engine.apps.oreonworlds.shaders.rocks;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.engine.engine.core.RenderingEngine;
import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.Constants;
import org.oreon.engine.engine.utils.ResourceLoader;
import org.oreon.engine.modules.instancing.InstancingCluster;
import org.oreon.engine.modules.terrain.Terrain;
import org.oreon.engine.modules.water.UnderWater;

public class RockHighPolyShader extends Shader{

	private static RockHighPolyShader instance = null;

	public static RockHighPolyShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new RockHighPolyShader();
	    }
	      return instance;
	}
	
	protected RockHighPolyShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockHighPoly_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockHighPoly_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockHighPoly_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("material.normalmap");
		addUniform("material.shininess");
		addUniform("material.emission");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		addUniform("isRefraction");
		addUniform("isCameraUnderWater");
		
		addUniform("caustics");
		addUniform("dudvCaustics");
		addUniform("distortionCaustics");
		
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
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		setUniformi("isReflection", RenderingEngine.isWaterReflection() ? 1 : 0);
		setUniformi("isRefraction", RenderingEngine.isWaterRefraction() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getTransform().getScaling()));
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		setUniformf("sightRangeFactor", Terrain.getInstance().getConfiguration().getSightRangeFactor());
		
		setUniformi("isCameraUnderWater", RenderingEngine.isCameraUnderWater() ? 1 : 0);
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material.normalmap", 1);
		
		setUniformf("material.shininess", material.getShininess());
		setUniformf("material.emission", material.getEmission());
		
		glActiveTexture(GL_TEXTURE2);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 2);

		setUniformf("distortionCaustics", UnderWater.getInstance().getDistortion());
		
		glActiveTexture(GL_TEXTURE3);
		UnderWater.getInstance().getCausticsMap().bind();
		setUniformi("caustics", 3);
		glActiveTexture(GL_TEXTURE4);
		UnderWater.getInstance().getDudvMap().bind();
		setUniformi("dudvCaustics", 4);
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
