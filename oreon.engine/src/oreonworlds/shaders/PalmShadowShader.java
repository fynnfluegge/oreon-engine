package oreonworlds.shaders;

import engine.scenegraph.GameObject;
import engine.shadersamples.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

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
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/Palm_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/PalmShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/PalmShadow_FS.glsl"));
		compileShader();
		
		addUniform("worldMatrix");
		
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
	}
	
	public void updateUniforms(GameObject object){
		
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
	}
}
