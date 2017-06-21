package org.oreon.engine.engine.shaders.blinnphong;

import org.oreon.engine.modules.lighting.DirectionalLight;
import org.oreon.engine.engine.core.Camera;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

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

