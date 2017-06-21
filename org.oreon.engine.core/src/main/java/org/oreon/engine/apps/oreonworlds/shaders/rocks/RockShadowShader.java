package org.oreon.engine.apps.oreonworlds.shaders.rocks;


import java.util.List;

import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.Constants;
import org.oreon.engine.engine.utils.ResourceLoader;
import org.oreon.engine.modules.instancing.InstancingCluster;

public class RockShadowShader extends Shader{

	private static RockShadowShader instance;

	public static RockShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new RockShadowShader();
	    }
	     return instance;
	}
	
	protected RockShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockShadow_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_Shader/RockShadow_FS.glsl"));
		compileShader();
		
		addUniformBlock("worldMatrices");
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(GameObject object){
		
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		
		List<Integer> indices = ((InstancingCluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
