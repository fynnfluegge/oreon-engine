package oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;
import oreonworlds.assets.plants.PalmInstanced;

public class PalmBillboardShader extends Shader{

private static PalmBillboardShader instance = null;
	
	public static PalmBillboardShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmBillboardShader();
	    }
	      return instance;
	}
	
	protected PalmBillboardShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01Billboard_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("clipplane");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniform("material.diffusemap");
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("worldMatrices", ((PalmInstanced) object.getParent()).getBillboardWorldMatBinding());
		bindUniformBlock("modelMatrices", ((PalmInstanced) object.getParent()).getBillboardModelMatBinding());
		
		Material material = (Material) object.getComponent("Material");
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
	}
}
