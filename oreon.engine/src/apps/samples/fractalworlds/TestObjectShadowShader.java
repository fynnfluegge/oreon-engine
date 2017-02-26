package apps.samples.fractalworlds;

import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class TestObjectShadowShader extends Shader{

	private static TestObjectShadowShader instance;

	public static TestObjectShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TestObjectShadowShader();
	    }
	     return instance;
	}
	
	protected TestObjectShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/TBN/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("samples/FractalWorlds/testObject/Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("samples/FractalWorlds/testObject/Shadow_FS.glsl"));
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
