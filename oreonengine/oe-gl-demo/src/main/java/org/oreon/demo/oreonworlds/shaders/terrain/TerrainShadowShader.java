package org.oreon.demo.oreonworlds.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.GameObject;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.terrain.TerrainConfiguration;
import org.oreon.modules.terrain.TerrainNode;

public class TerrainShadowShader extends GLShader{
	
	private static TerrainShadowShader instance = null;
	
	public static TerrainShadowShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainShadowShader();
	    }
	      return instance;
	}
	
protected TerrainShadowShader(){
		
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/terrain/TerrainShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/terrain/TerrainGrid_FS.glsl"));
		compileShader();

		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
		}
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("lod");
		addUniform("index");
		addUniform("location");
		addUniform("gap");
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniformBlock("Camera");
		addUniformBlock("LightViewProjections");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);

		setUniform("localMatrix", object.getLocalTransform().getWorldMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());

		TerrainNode terrainNode = (TerrainNode) object;
		TerrainConfiguration terrConfig = terrainNode.getTerrConfig();
		int lod = terrainNode.getLod();
		Vec2f index = terrainNode.getIndex();
		Vec2f location = terrainNode.getLocation();
		float gap = terrainNode.getGap();
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i);	
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
		}
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformi("bezier", terrConfig.getBezier());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniform("location", location);
		setUniformf("gap", gap);
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
	}


}
