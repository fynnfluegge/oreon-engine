package oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.lighting.DirectionalLight;
import modules.terrain.Terrain;
import engine.core.Constants;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shadersamples.Shader;
import engine.utils.ResourceLoader;

public class PalmBushInstancedShader extends Shader{
	
private static PalmBushInstancedShader instance = null;
	
	public static PalmBushInstancedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmBushInstancedShader();
	    }
	      return instance;
	}
	
	protected PalmBushInstancedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBush_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBush_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBush_FS.glsl"));
		compileShader();
		
		addUniform("worldMatrix");
		addUniform("modelMatrix");
		addUniform("sightRangeFactor");
		addUniform("directionalLight.intensity");
		addUniform("directionalLight.color");
		addUniform("directionalLight.direction");
		addUniform("directionalLight.ambient");
		addUniform("material.diffusemap");
//		addUniform("material.emission");
//		addUniform("material.shininess");
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("modelMatrix", object.getTransform().getModelMatrix());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		setUniform("directionalLight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("directionalLight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("directionalLight.color", DirectionalLight.getInstance().getColor());
		setUniform("directionalLight.direction", DirectionalLight.getInstance().getDirection());
		
		Material material = (Material) object.getComponent("Material");

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
	}
}
