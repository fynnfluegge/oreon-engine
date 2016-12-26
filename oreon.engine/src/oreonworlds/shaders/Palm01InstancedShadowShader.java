package oreonworlds.shaders;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Palm01InstancedShadowShader extends Shader{

	private static Palm01InstancedShadowShader instance;

	public static Palm01InstancedShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Palm01InstancedShadowShader();
	    }
	     return instance;
	}
	
	protected Palm01InstancedShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01Shadow_FS.glsl"));
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
		bindUniformBlock("InstancedMatrices", Constants.Palm01InstancedMatrices);
	}
}
