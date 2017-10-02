package org.oreon.demo.gl.oreonworlds.shaders.plants;

import java.util.List;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.demo.gl.oreonworlds.assets.plants.Bush01Cluster;

public class BushShadowShader extends GLShader{
	
	private static BushShadowShader instance;

	public static BushShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BushShadowShader();
	    }
	     return instance;
	}
	
	protected BushShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_Shader/Bush01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_Shader/Bush01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_Shader/Bush01Shadow_FS.glsl"));
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
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		
		List<Integer> indices = ((Bush01Cluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}

}
