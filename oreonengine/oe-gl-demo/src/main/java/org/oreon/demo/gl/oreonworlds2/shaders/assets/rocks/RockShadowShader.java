package org.oreon.demo.gl.oreonworlds2.shaders.assets.rocks;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.scene.GameObject;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class RockShadowShader extends GLShader{

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
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockShadow_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds2/shaders/assets/Rock_Shader/RockShadow_FS.glsl"));
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
