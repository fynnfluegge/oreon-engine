package org.oreon.core.shaders.blinnphong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.light.DirectionalLight;
import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;

public class BlinnPhongTexturedShader extends Shader{
	
private static BlinnPhongTexturedShader instance = null;
	
	public static BlinnPhongTexturedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BlinnPhongTexturedShader();
	    }
	      return instance;
	}
	
	protected BlinnPhongTexturedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/texture/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/blinn-phong/texture/Fragment.glsl"));
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
	
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("eyePosition", CoreSystem.getInstance().getScenegraph().getCamera().getPosition());
		setUniform("directionalLight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", DirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", DirectionalLight.getInstance().getDirection());
		
		Material material = (Material) object.getComponent("Material");

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
