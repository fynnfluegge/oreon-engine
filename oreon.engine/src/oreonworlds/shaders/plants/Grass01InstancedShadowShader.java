package oreonworlds.shaders.plants;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Grass01InstancedShadowShader extends Shader{

	private static Grass01InstancedShadowShader instance = null;
	
	public static Grass01InstancedShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Grass01InstancedShadowShader();
	    }
	      return instance;
	}
	
	protected Grass01InstancedShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_01/Bush01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_01/Bush01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Bush_01/Bush01Shadow_FS.glsl"));
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
		bindUniformBlock("InstancedMatrices", Constants.Grass01InstancedMatricesBinding);
	}
}
