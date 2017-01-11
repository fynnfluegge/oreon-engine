package engine.shader.blinnphong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.lighting.DirectionalLight;
import engine.core.Camera;
import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.ResourceLoader;


public class BlinnPhongTessellationShader extends Shader{
	
	private static BlinnPhongTessellationShader instance = null;
	
	public static BlinnPhongTessellationShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BlinnPhongTessellationShader();
	    }
	      return instance;
	}
	
	protected BlinnPhongTessellationShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/blinn-phong/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/blinn-phong/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/blinn-phong/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/blinn-phong/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/blinn-phong/tessellation/Fragment.glsl"));
		compileShader();
		
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		
		addUniform("material.diffusemap");
		addUniform("material.emission");
		addUniform("material.shininess");
		addUniform("material.normalmap");
		addUniform("material.specularmap");
		addUniform("material.displacemap");
		addUniform("material.displaceScale");
		addUniform("specularmap");
		addUniform("displacement");
		addUniform("diffusemap");
		addUniform("normalmap");
		
		addUniform("clipplane");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void updateUniforms(GameObject object)
	{
		
		setUniform("viewProjectionMatrix", Camera.getInstance().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		setUniform("directionalLight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", DirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", DirectionalLight.getInstance().getDirection());	
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
		
		Material material = (Material) object.getComponent("Material");
		
		setUniformi("tessFactor", 100);
		setUniformf("tessSlope", 0.2f);
		setUniformf("tessShift", 1);
		
		if (material.getDiffusemap() != null){
			setUniformi("diffusemap", 1);
			glActiveTexture(GL_TEXTURE0);
			material.getDiffusemap().bind();
			setUniformi("material.diffusemap", 0);
		}
		else
			setUniformi("diffusemap", 0);
		if (material.getNormalmap() != null){
			setUniformi("normalmap", 1);
			glActiveTexture(GL_TEXTURE1);
			material.getNormalmap().bind();
			setUniformi("material.normalmap", 1);
		}
		else
			setUniformi("normalmap", 0);
		
		if (material.getSpecularmap() != null){
			setUniformi("specularmap", 1);
			glActiveTexture(GL_TEXTURE2);
			material.getSpecularmap().bind();
			setUniformi("material.specularmap", 2);
		}
		else
			setUniformi("specularmap", 0);
			
		if (material.getDisplacemap() != null){
			setUniformi("displacement", 1);
			glActiveTexture(GL_TEXTURE3);
			material.getDisplacemap().bind();
			setUniformi("material.displacemap", 3);
			setUniformf("material.displaceScale", material.getDisplaceScale());
		}
		else
			setUniformi("displacement", 0);
		
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
	}
}
