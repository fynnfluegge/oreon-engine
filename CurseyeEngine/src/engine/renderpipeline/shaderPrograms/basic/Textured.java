package engine.renderpipeline.shaderPrograms.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.models.data.Material;
import engine.renderpipeline.Shader;

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

		addVertexShader(ResourceLoader.loadShader("basic/texture/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("basic/texture/Fragment.glsl"));
		compileShader();
		
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("texture");
		addUniform("clipplane");
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("modelViewProjectionMatrix", modelViewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("clipplane", RenderingEngine.getClipplane());	
	}
	
	public void sendUniforms(Material material)
	{
		glActiveTexture(GL_TEXTURE0 );
		material.getDiffusemap().bind();
		setUniformi("texture", 0);
	}
}
