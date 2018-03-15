package org.oreon.gl.demo.oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.gl.terrain.TerrainConfiguration;
import org.oreon.modules.gl.terrain.TerrainNode;
import org.oreon.modules.gl.water.UnderWater;

public class TerrainShader extends GLShader{
	
	private static TerrainShader instance = null;
	
	public static TerrainShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainShader();
	    }
	      return instance;
	}
	
	protected TerrainShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_FS.glsl"));
		compileShader();
		
		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		
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
		addUniform("isRefraction");
		addUniform("isCameraUnderWater");
		
		addUniform("caustics");
		addUniform("dudvCaustics");
		addUniform("distortionCaustics");
		
		addUniform("heightmap");
		addUniform("normalmap");
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		for (int i=0; i<4; i++){
			addUniform("materials[" + i + "].diffusemap");
			addUniform("materials[" + i + "].alphamap");
			addUniform("materials[" + i + "].normalmap");
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
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderEngine().getClipplane());
		setUniformi("isRefraction", CoreSystem.getInstance().getRenderEngine().isWaterRefraction() ? 1 : 0);
		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderEngine().isCameraUnderWater() ? 1 : 0);		
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
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
		terrConfig.getNormalmap().bind();
		setUniformi("normalmap", 1);
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
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
		
		UnderWater underwater = (UnderWater) CoreSystem.getInstance().getRenderEngine().getUnderwater();
		
		glActiveTexture(GL_TEXTURE2);
		underwater.getCausticsMap().bind();
		setUniformi("caustics", 2);
		glActiveTexture(GL_TEXTURE3);
		underwater.getDudvMap().bind();
		setUniformi("dudvCaustics", 3);
		
		setUniformf("distortionCaustics", underwater.getDistortion());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
		
		int texUnit = 4;
		for (int i=0; i<4; i++){
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getDiffusemap().bind();
			setUniformi("materials[" + i + "].diffusemap", texUnit);
			texUnit++;
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getHeightmap().bind();
			setUniformi("materials[" + i + "].heightmap", texUnit);
			texUnit++;
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getAlphamap().bind();
			setUniformi("materials[" + i + "].alphamap", texUnit);
			texUnit++;
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getNormalmap().bind();
			setUniformi("materials[" + i + "].normalmap", texUnit);
			texUnit++;
			
			setUniformf("materials[" + i + "].heightScaling", terrConfig.getMaterials().get(i).getHeightScaling());
			setUniformf("materials[" + i + "].horizontalScaling", terrConfig.getMaterials().get(i).getHorizontalScaling());
		}
	}
}
