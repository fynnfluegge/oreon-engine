package engine.shaders.blinnphong;

import modules.lighting.DirectionalLight;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shaders.Shader;

public class RGBA extends Shader{
	
	private static RGBA instance;

	public static RGBA getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new RGBA();
	    }
	     return instance;
	}
	
	protected RGBA()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/rgba/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/blinn-phong/rgba/Fragment.glsl"));
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
