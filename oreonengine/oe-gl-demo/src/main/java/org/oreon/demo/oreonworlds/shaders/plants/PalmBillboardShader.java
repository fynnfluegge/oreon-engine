package org.oreon.demo.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
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

public class PalmBillboardShader extends GLShader{

	private static PalmBillboardShader instance = null;
	
	public static PalmBillboardShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmBillboardShader();
	    }
	      return instance;
	}
	
	protected PalmBillboardShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_Shader/Palm01Billboard_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("clipplane");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniform("material.diffusemap");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getConfiguration().getSightRangeFactor());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		setUniformi("isReflection", CoreSystem.getInstance().getRenderingEngine().isWaterReflection() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		Material material = (Material) object.getComponent("Material");
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getLowPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
