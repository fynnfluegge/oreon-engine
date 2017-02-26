package oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import oreonworlds.assets.plants.PalmInstanced;

public class PalmBillboardShadowShader extends Shader{

	private static PalmBillboardShadowShader instance = null;
	
	public static PalmBillboardShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PalmBillboardShadowShader();
	    }
	      return instance;
	}
	
	protected PalmBillboardShadowShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01BillboardShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01BillboardShadow_FS.glsl"));
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
		bindUniformBlock("InstancedMatrices",((PalmInstanced) object.getParent()).getBillboardWorldMatBinding());
		
		Material material = (Material) object.getComponent("Material");
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
	}
}
