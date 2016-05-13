package engine.shaders.phong;

import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.components.Material;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.shaders.Shader;

public class Glass extends Shader{
	
	private static Glass instance;

	public static Glass getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Glass();
	    }
	     return instance;
	}
	
	protected Glass()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("phong/glass/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("phong/glass/Fragment.glsl"));
		compileShader();
		
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		addUniform("material.color");
		addUniform("material.emission");
		addUniform("material.shininess");
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
		setUniform("material.color", material.getColor());
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
	}

}

