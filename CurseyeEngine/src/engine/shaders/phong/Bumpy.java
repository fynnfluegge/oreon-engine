package engine.shaders.phong;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.scenegraph.components.Material;
import engine.shaders.Shader;

public class Bumpy extends Shader{
	
	private static Bumpy instance;

	public static Bumpy getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Bumpy();
	    }
	     return instance;
	}
	
	protected Bumpy()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("phong/bumpy/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("phong/bumpy/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("phong/bumpy/Fragment.glsl"));
		compileShader();
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
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
		
		setUniformf("material2.emission", material.getEmission());
		setUniformf("material2.shininess", material.getShininess());
	}
}
