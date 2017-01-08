package oreonworlds.shaders.rocks;

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

public class Rock02InstancedShader extends Shader{
	
	private static Rock02InstancedShader instance = null;
	
	public static Rock02InstancedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Rock02InstancedShader();
	    }
	      return instance;
	}
	
	protected Rock02InstancedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Rock_02/Rock02_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.diffusemap");
		addUniform("material.normalmap");
		addUniform("clipplane");
		addUniform("material.emission");
		addUniform("material.shininess");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Rock02InstancedMatricesBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		material.getNormalmap().bind();
		setUniformi("material.normalmap", 1);
		
		setUniformf("material.emission", material.getEmission());
		setUniformf("material.shininess", material.getShininess());
	}
}
