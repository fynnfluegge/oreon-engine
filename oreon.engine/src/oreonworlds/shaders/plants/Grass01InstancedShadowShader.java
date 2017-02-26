package oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Grass01InstancedShadowShader extends Shader{

	private static Grass01InstancedShadowShader instance = null;
	
	public static Grass01InstancedShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Grass01InstancedShadowShader();
	    }
	      return instance;
	}
	
	protected Grass01InstancedShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/Grass_01/Grass01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/Grass_01/Grass01Shadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/Grass_01/Grass01Shadow_FS.glsl"));
		compileShader();
		
		addUniform("clipplane");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
		addUniform("material.diffusemap");
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Plant01InstancedMatricesBinding);
		
		Material material = (Material) object.getComponent("Material");
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
	}
}
