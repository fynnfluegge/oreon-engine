package oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;
import oreonworlds.assets.plants.PalmInstanced;

public class PalmShader extends Shader{

private static PalmShader instance = null;
	
	public static PalmShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmShader();
	    }
	      return instance;
	}
	
	protected PalmShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.color");
		addUniform("clipplane");
//		addUniform("material.emission");
//		addUniform("material.shininess");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
		addUniform("shadowMaps");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("worldMatrices", ((PalmInstanced) object.getParent()).getHighpolyWorldMatBinding());
		bindUniformBlock("modelMatrices", ((PalmInstanced) object.getParent()).getHighpolyModelMatBinding());
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");
		setUniform("material.color", material.getColor());
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
		
		glActiveTexture(GL_TEXTURE1);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 1);
	}
}
