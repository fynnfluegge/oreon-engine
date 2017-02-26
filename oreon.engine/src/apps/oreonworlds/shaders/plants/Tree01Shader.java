package apps.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import apps.oreonworlds.assets.plants.Tree01Instanced;
import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;

public class Tree01Shader extends Shader{
	
	private static Tree01Shader instance = null;

	public static Tree01Shader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Tree01Shader();
	    }
	      return instance;
	}
	
	protected Tree01Shader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Tree_01/Tree01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Tree_01/Tree01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Tree_01/Tree01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("clipplane");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
//		addUniform("shadowMaps");
	}	
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("worldMatrices", ((Tree01Instanced) object.getParent()).getHighpolyWorldMatBinding());
		bindUniformBlock("modelMatrices", ((Tree01Instanced) object.getParent()).getHighpolyModelMatBinding());
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
//		glActiveTexture(GL_TEXTURE1);
//		RenderingEngine.getShadowMaps().getDepthMaps().bind();
//		setUniformi("shadowMaps", 1);
	}
}
