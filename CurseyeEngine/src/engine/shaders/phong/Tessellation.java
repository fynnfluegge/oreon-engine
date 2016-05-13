package engine.shaders.phong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.components.Material;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.shaders.Shader;


public class Tessellation extends Shader{
	
	private static Tessellation instance = null;
	
	public static Tessellation getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Tessellation();
	    }
	      return instance;
	}
	
	protected Tessellation()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("phong/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("phong/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("phong/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("phong/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("phong/tessellation/Fragment.glsl"));
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
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f viewProjectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		
		setUniform("viewProjectionMatrix", viewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		setUniform("directionalLight.ambient", RenderingEngine.getDirectionalLight().getAmbient());
		setUniformf("directionalLight.intensity", RenderingEngine.getDirectionalLight().getIntensity());
		setUniform("directionalLight.color", RenderingEngine.getDirectionalLight().getColor());
		setUniform("directionalLight.direction", RenderingEngine.getDirectionalLight().getDirection());	
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(Material material)
	{	
		setUniformi("tessFactor", 400);
		setUniformf("tessSlope", 0.7f);
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
