package org.oreon.core.gl.shaders.blinnphong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.ResourceLoader;

public class BlinnPhongBumpShader extends GLShader{
	
	private static BlinnPhongBumpShader instance;

	public static BlinnPhongBumpShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BlinnPhongBumpShader();
	    }
	     return instance;
	}
	
	protected BlinnPhongBumpShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/TBN/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/blinn-phong/TBN/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/blinn-phong/TBN/Fragment.glsl"));
		compileShader();
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("modelMatrix");
		addUniform("eyePosition");
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		addUniform("material2.diffusemap");
		addUniform("material2.emission");
		addUniform("material2.shininess");
		addUniform("material2.normalmap");
		addUniform("material2.specularmap");
		addUniform("material1.displacemap");
		addUniform("material1.displaceScale");
		addUniform("specularmap");
		addUniform("displacement");
		addUniform("diffusemap");
		addUniform("clipplane");
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("viewProjectionMatrix", CoreSystem.getInstance().getScenegraph().getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("modelMatrix", object.getWorldTransform().getModelMatrix());
		setUniform("eyePosition", CoreSystem.getInstance().getScenegraph().getCamera().getPosition());
		setUniform("directionalLight.ambient", GLDirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", GLDirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", GLDirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", GLDirectionalLight.getInstance().getDirection());
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", CoreSystem.getInstance().getScenegraph().getCamera().getFrustumPlanes()[i]);
		}
		
		Material material = (Material) object.getComponents().get("Material");
		
		if (material.getDiffusemap() != null){
			setUniformi("diffusemap", 1);
			glActiveTexture(GL_TEXTURE0);
			material.getDiffusemap().bind();
			setUniformi("material2.diffusemap", 0);
		}
		else
			setUniformi("diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material2.normalmap", 1);
		
		if (material.getSpecularmap() != null){
			setUniformi("specularmap", 1);
			glActiveTexture(GL_TEXTURE2);
			material.getSpecularmap().bind();
			setUniformi("material2.specularmap", 2);
		}
		else
			setUniformi("specularmap", 0);
			
		if (material.getDisplacemap() != null){
			setUniformi("displacement", 1);
			glActiveTexture(GL_TEXTURE3);
			material.getDisplacemap().bind();
			setUniformi("material1.displacemap", 3);
		}
		else
			setUniformi("displacement", 0);
		
		setUniformf("material1.displaceScale", 0);
		setUniformf("material2.emission", material.getEmission());
		setUniformf("material2.shininess", material.getShininess());
	}
}
