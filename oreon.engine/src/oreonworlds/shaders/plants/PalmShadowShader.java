package oreonworlds.shaders.plants;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import oreonworlds.assets.plants.PalmInstanced;

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
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		bindUniformBlock("worldMatrices", ((PalmInstanced) object.getParent()).getHighpolyWorldMatBinding());
	}
}
