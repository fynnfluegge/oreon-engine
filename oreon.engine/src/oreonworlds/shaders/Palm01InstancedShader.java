package oreonworlds.shaders;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shadersamples.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;

public class Palm01InstancedShader extends Shader{

private static Palm01InstancedShader instance = null;
	
	public static Palm01InstancedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Palm01InstancedShader();
	    }
	      return instance;
	}
	
	protected Palm01InstancedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm_01/Palm01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.color");
		addUniform("clipplane");
//		addUniform("material.emission");
//		addUniform("material.shininess");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("InstancedMatrices");
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("InstancedMatrices", Constants.Palm01InstancedMatrices);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");
		setUniform("material.color", material.getColor());
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
	}
}
