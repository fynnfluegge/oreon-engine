package engine.shaders.blinnphong;

import engine.components.light.DirectionalLight;
import engine.components.model.Material;
import engine.core.Camera;
import engine.scene.GameObject;
import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class GlassShader extends Shader{
	
	private static GlassShader instance;

	public static GlassShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GlassShader();
	    }
	     return instance;
	}
	
	protected GlassShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/glass/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/blinn-phong/glass/Fragment.glsl"));
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
	
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("eyePosition", Camera.getInstance().getPosition());
		setUniform("directionalLight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", DirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", DirectionalLight.getInstance().getDirection());
		Material material = (Material) object.getComponent("Material");
		setUniform("material.color", material.getColor());
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
	}
}

