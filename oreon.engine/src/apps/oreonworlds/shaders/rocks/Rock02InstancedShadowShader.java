package apps.oreonworlds.shaders.rocks;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Rock02InstancedShadowShader extends Shader{
	
	private static Rock02InstancedShadowShader instance = null;
	
	public static Rock02InstancedShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Rock02InstancedShadowShader();
	    }
	      return instance;
	}
	
	protected Rock02InstancedShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02Shadow_FS.glsl"));
		compileShader();
		
		addUniform("pssm_splits");
		addUniform("clipplane");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
	}
	
	public void updateUniforms(GameObject object){
		
		setUniformi("pssm_splits", Constants.PSSM_SPLITS);
		setUniform("clipplane", RenderingEngine.getClipplane());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Rock02InstancedMatricesBinding);
	}

}

