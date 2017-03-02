package apps.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import apps.oreonworlds.assets.plants.Tree01Instanced;
import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.terrain.Terrain;

public class TreeBillboardShader extends Shader{

private static TreeBillboardShader instance = null;
	
	public static TreeBillboardShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TreeBillboardShader();
	    }
	      return instance;
	}
	
	protected TreeBillboardShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Billboard_Shader/Billboard_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Billboard_Shader/Billboard_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Billboard_Shader/Billboard_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("clipplane");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniform("material.diffusemap");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(GameObject object){
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("worldMatrices", ((Tree01Instanced) object.getParent()).getWorldMatBinding());
		bindUniformBlock("modelMatrices", ((Tree01Instanced) object.getParent()).getModelMatBinding());
		
		Material material = (Material) object.getComponent("Material");
		
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((Tree01Instanced) object.getParent()).getBillboardIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
