package org.oreon.gl.demo.oreonworlds.shaders;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class InstancingGridShader extends GLShader{

private static InstancingGridShader instance = null;
	
	public static InstancingGridShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new InstancingGridShader();
	    }
	      return instance;
	}
	
	protected InstancingGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancingGrid_Shader/Grid_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancingGrid_Shader/Grid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/InstancingGrid_Shader/Grid_FS.glsl"));
		compileShader();
		
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		
		for (int i=0; i<500; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}	
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		setUniformi("isReflection", CoreSystem.getInstance().getRenderEngine().isWaterReflection() ? 1 : 0);
		
		((InstancedCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancedCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderEngine().getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		List<Integer> indices = ((InstancedCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
