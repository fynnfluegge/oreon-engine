package org.oreon.gl.demo.oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.NodeComponentKey;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.gl.components.terrain.TerrainConfiguration;
import org.oreon.gl.components.terrain.TerrainNode;

public class TerrainWireframeShader extends GLShaderProgram{
	
private static TerrainWireframeShader instance = null;
	
	public static TerrainWireframeShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainWireframeShader();
	    }
	      return instance;
	}
	
	protected TerrainWireframeShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/TerrainWireframe_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/TerrainWireframe_FS.glsl"));
		compileShader();
		
		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleXZ");
		addUniform("scaleY");
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailRange");
		addUniform("index");
		addUniform("gap");
		addUniform("lod");
		addUniform("location");
		addUniform("texDetail");
		addUniform("waterReflectionShift");
		
		addUniform("heightmap");
		addUniform("splatmap");
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		for (int i=0; i<4; i++){
			addUniform("materials[" + i + "].heightmap");
			addUniform("materials[" + i + "].heightScaling");
			addUniform("materials[" + i + "].horizontalScaling");
		}
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(Renderable object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		
		setUniform("clipplane", EngineContext.getRenderState().getClipplane());
		
		TerrainConfiguration terrConfig = object.getComponent(NodeComponentKey.CONFIGURATION);
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		float gap = ((TerrainNode) object).getGap();
		Vec2f location = ((TerrainNode) object).getLocation();
		
		setUniform("localMatrix", object.getLocalTransform().getWorldMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
			
		glActiveTexture(GL_TEXTURE0);
		terrConfig.getHeightmap().bind();
		setUniformi("heightmap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		terrConfig.getSplatmap().bind();
		setUniformi("splatmap", 1);
		
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformi("bezier", terrConfig.getBezier());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		setUniformi("waterReflectionShift", terrConfig.getWaterReflectionShift());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
		
		int texUnit = 4;
		for (int i=0; i<4; i++){
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getHeightmap().bind();
			setUniformi("materials[" + i + "].heightmap", texUnit);
			texUnit++;
			
			setUniformf("materials[" + i + "].heightScaling", terrConfig.getMaterials().get(i).getHeightScaling());
			setUniformf("materials[" + i + "].horizontalScaling", terrConfig.getMaterials().get(i).getHorizontalScaling());
		}
	}

}
