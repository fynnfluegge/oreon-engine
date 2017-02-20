package oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.terrain.Terrain;
import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class Bush01InstancedShader extends Shader{
	
	private static Bush01InstancedShader instance = null;
	
	public static Bush01InstancedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Bush01InstancedShader();
	    }
	      return instance;
	}
	
	protected Bush01InstancedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/Bush_01/Bush01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/Bush_01/Bush01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/Bush_01/Bush01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("clipplane");
		addUniform("material.emission");
		addUniform("material.shininess");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
		addUniform("shadowMaps");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Bush01InstancedMatricesBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
		
		glActiveTexture(GL_TEXTURE1);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 1);
	}
}
