package org.oreon.gl.demo.oreonworlds.shaders.assets.rocks;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
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
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Rock_Shader/RockHighPoly_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Rock_Shader/RockHighPoly_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Rock_Shader/RockHighPoly_FS.glsl"));
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
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		((InstancedCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancedCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniformi("isReflection", CoreSystem.getInstance().getRenderEngine().isWaterReflection() ? 1 : 0);
		setUniformi("isRefraction", CoreSystem.getInstance().getRenderEngine().isWaterRefraction() ? 1 : 0);
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		setUniform("clipplane", CoreSystem.getInstance().getRenderEngine().getClipplane());
		
		
		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderEngine().isCameraUnderWater() ? 1 : 0);
		
		Material material = (Material) object.getComponent(ComponentType.MATERIAL0);

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material.normalmap", 1);
		
		setUniformf("material.shininess", material.getShininess());
		setUniformf("material.emission", material.getEmission());
		
		UnderWater underwater = (UnderWater) CoreSystem.getInstance().getRenderEngine().getUnderwater();
		
		glActiveTexture(GL_TEXTURE2);
		underwater.getCausticsMap().bind();
		setUniformi("caustics", 2);
		glActiveTexture(GL_TEXTURE3);
		underwater.getDudvMap().bind();
		setUniformi("dudvCaustics", 3);
		
		setUniformf("distortionCaustics", underwater.getDistortion());
		
		List<Integer> indices = ((InstancedCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
