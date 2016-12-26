package engine.shader.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class Textured extends Shader{

	private static Textured instance = null;
	
	public static Textured getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Textured();
	    }
	      return instance;
	}
	
	protected Textured()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/texture/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/texture/Fragment.glsl"));
		compileShader();
		
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("texture");
		addUniform("clipplane");
	}
	
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("clipplane", RenderingEngine.getClipplane());	
		
		glActiveTexture(GL_TEXTURE0 );
		((Material) object.getComponents().get("Material")).getDiffusemap().bind();
		setUniformi("texture", 0);
	}
}
