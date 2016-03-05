package engine.shaderprograms.phong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.components.Material;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.shaderprograms.Shader;

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

		addVertexShader(ResourceLoader.loadShader("phong/texture/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("phong/texture/Fragment.glsl"));
		compileShader();
		
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		addUniform("material.diffusemap");
		addUniform("material.specularmap");
		addUniform("material.emission");
		addUniform("material.shininess");
		addUniform("specularmap");
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("modelViewProjectionMatrix", modelViewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		setUniform("directionalLight.ambient", RenderingEngine.getDirectionalLight().getAmbient());
		setUniformf("directionalLight.intensity", RenderingEngine.getDirectionalLight().getIntensity());
		setUniform("directionalLight.color", RenderingEngine.getDirectionalLight().getColor());
		setUniform("directionalLight.direction", RenderingEngine.getDirectionalLight().getDirection());
	}
	
	public void sendUniforms(Material material)
	{
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
		
		if (material.getSpecularmap() != null){
			setUniformi("specularmap", 1);
			glActiveTexture(GL_TEXTURE1);
			material.getSpecularmap().bind();
			setUniformi("material.specularmap", 1);
		}
		else
			setUniformi("specularmap", 0);
	}
}
