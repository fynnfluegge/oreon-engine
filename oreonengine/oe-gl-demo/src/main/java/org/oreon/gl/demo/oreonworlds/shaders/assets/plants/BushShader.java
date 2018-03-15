package org.oreon.gl.demo.oreonworlds.shaders.assets.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scene.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class BushShader extends GLShader{
	
private static BushShader instance = null;
	
	public static BushShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BushShader();
	    }
	      return instance;
	}
	
	protected BushShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Bush_Shader/Bush01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Bush_Shader/Bush01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Bush_Shader/Bush01_FS.glsl"));
		compileShader();
		
		addUniform("material.diffusemap");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		addUniform("isRefraction");
		addUniform("isCameraUnderWater");
		
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
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformi("isReflection", CoreSystem.getInstance().getRenderEngine().isWaterReflection() ? 1 : 0);
		setUniformi("isRefraction", CoreSystem.getInstance().getRenderEngine().isWaterRefraction() ? 1 : 0);
		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderEngine().isCameraUnderWater() ? 1 : 0);	
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderEngine().getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		Material material = (Material) object.getComponent(Constants.MATERIAL);
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}

}
