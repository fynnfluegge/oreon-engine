package apps.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;

public class Grass01InstancedShader extends Shader{
	
	private static Grass01InstancedShader instance = null;
	
	public static Grass01InstancedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Grass01InstancedShader();
	    }
	      return instance;
	}
	
	protected Grass01InstancedShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Grass_01/Grass01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Grass_01/Grass01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Grass_01/Grass01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("clipplane");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
		addUniform("shadowMaps");
	}	
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Plant01InstancedMatricesBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", 1000);//Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 1);
	}
}
