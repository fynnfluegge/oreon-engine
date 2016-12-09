package oreonworlds.shaders;

import engine.core.Camera;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shadersamples.Shader;
import engine.utils.ResourceLoader;
import modules.lighting.DirectionalLight;
import modules.terrain.Terrain;

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

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/Palm_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/Palm_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Palm/Palm_FS.glsl"));
		compileShader();
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("modelMatrix");
		addUniform("eyePosition");
		addUniform("sightRangeFactor");
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		addUniform("material.color");
//		addUniform("material.emission");
//		addUniform("material.shininess");
	}
	
	public void updateUniforms(GameObject object)
	{
		setUniform("viewProjectionMatrix", Camera.getInstance().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("modelMatrix", object.getTransform().getModelMatrix());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		setUniform("eyePosition", Camera.getInstance().getPosition());
		setUniform("directionalLight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", DirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", DirectionalLight.getInstance().getDirection());
		
		Material material = (Material) object.getComponent("Material");
		setUniform("material.color", material.getColor());
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
	}
}
