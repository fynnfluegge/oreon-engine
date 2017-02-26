package apps.oreonworlds.shaders.rocks;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Rock01InstancedShadowShader extends Shader{
	
	private static Rock01InstancedShadowShader instance = null;
	
	public static Rock01InstancedShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Rock01InstancedShadowShader();
	    }
	      return instance;
	}
	
	protected Rock01InstancedShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_01/Rock01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_01/Rock01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_01/Rock01Shadow_FS.glsl"));
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
		bindUniformBlock("InstancedMatrices", Constants.Rock01InstancedMatricesBinding);
	}

}
