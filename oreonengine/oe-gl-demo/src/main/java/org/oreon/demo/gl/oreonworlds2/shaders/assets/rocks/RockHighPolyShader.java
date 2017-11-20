package org.oreon.demo.gl.oreonworlds2.shaders.assets.rocks;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
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
import org.oreon.modules.gl.water.UnderWater;

public class RockHighPolyShader extends GLShader{

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
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockHighPoly_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockHighPoly_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockHighPoly_FS.glsl"));
		compileShader();
		
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
		
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		
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
		
		setUniformi("isReflection", CoreSystem.getInstance().getRenderingEngine().isWaterReflection() ? 1 : 0);
		setUniformi("isRefraction", CoreSystem.getInstance().getRenderingEngine().isWaterRefraction() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		
		
		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderingEngine().isCameraUnderWater() ? 1 : 0);
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material.normalmap", 1);
		
		setUniformf("material.shininess", material.getShininess());
		setUniformf("material.emission", material.getEmission());

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
