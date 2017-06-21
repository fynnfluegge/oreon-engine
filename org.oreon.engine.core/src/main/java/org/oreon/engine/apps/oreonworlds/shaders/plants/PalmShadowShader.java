package org.oreon.engine.apps.oreonworlds.shaders.plants;

import java.util.List;

import org.oreon.engine.apps.oreonworlds.assets.plants.Palm01Cluster;
import org.oreon.engine.engine.core.RenderingEngine;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.Constants;
import org.oreon.engine.engine.utils.ResourceLoader;
import org.oreon.engine.modules.instancing.InstancingCluster;

public class PalmShadowShader extends Shader{

	private static PalmShadowShader instance;

	public static PalmShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmShadowShader();
	    }
	     return instance;
	}
	
	protected PalmShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01Shadow_FS.glsl"));
		compileShader();
		
		addUniform("clipplane");
		addUniformBlock("worldMatrices");
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		
		List<Integer> indices = ((Palm01Cluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
