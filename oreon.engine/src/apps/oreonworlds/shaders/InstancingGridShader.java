package apps.oreonworlds.shaders;


import java.util.List;

import engine.core.RenderingEngine;
import engine.math.Matrix4f;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.instancing.InstancingCluster;

public class InstancingGridShader extends Shader{

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

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/InstancingGrid_Shader/Grid_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/InstancingGrid_Shader/Grid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/InstancingGrid_Shader/Grid_FS.glsl"));
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
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		setUniformi("isReflection", RenderingEngine.isReflection() ? 1 : 0);
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getTransform().getScaling()));
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
