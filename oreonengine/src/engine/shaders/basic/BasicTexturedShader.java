package engine.shaders.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.components.model.Material;
import engine.core.RenderingEngine;
import engine.scene.GameObject;
import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class BasicTexturedShader extends Shader{

	private static BasicTexturedShader instance = null;
	
	public static BasicTexturedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BasicTexturedShader();
	    }
	      return instance;
	}
	
	protected BasicTexturedShader()
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
