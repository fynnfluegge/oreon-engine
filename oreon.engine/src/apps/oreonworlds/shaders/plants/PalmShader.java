package apps.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import apps.oreonworlds.assets.plants.Palm01Cluster;
import engine.core.RenderingEngine;
import engine.math.Matrix4f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.instancing.InstancingCluster;
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

		addVertexShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/assets/plants/Palm_01/Palm01_FS.glsl"));
		compileShader();
		
		addUniform("sightRangeFactor");
		addUniform("material.color");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
//		addUniform("material.emission");
//		addUniform("material.shininess");
		
		addUniformBlock("DirectionalLight");
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("LightViewProjections");
		addUniformBlock("Camera");
		addUniform("shadowMaps");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformi("isReflection", RenderingEngine.isReflection() ? 1 : 0);
		
		((InstancingCluster) object.getParent()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((InstancingCluster) object.getParent()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getTransform().getScaling()));
		setUniformf("sightRangeFactor", Terrain.getInstance().getTerrainConfiguration().getSightRangeFactor());
		
		Material material = (Material) object.getComponent("Material");
		setUniform("material.color", material.getColor());
//		setUniformf("material.emission", material.getEmission());
//		setUniformf("material.shininess", material.getShininess());
		
		glActiveTexture(GL_TEXTURE1);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 1);
		
		List<Integer> indices = ((Palm01Cluster) object.getParent()).getHighPolyIndices();
		
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
