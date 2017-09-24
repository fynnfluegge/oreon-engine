package org.oreon.demo.oreonworlds.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE22;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.terrain.TerrainConfiguration;
import org.oreon.modules.terrain.TerrainNode;

public class TerrainGridShader extends GLShader{
	
	private static TerrainGridShader instance = null;
	
	public static TerrainGridShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainGridShader();
	    }
	      return instance;
	}
	
	protected TerrainGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/TerrainGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Terrain_Shader/TerrainGrid_FS.glsl"));
		compileShader();
		
		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
		}
		
		for (int i=0; i<4; i++)
		{
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		addUniform("largeDetailedRange");
		addUniform("texDetail");
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("lod");
		addUniform("index");
		addUniform("location");
		addUniform("gap");
		addUniform("waterReflectionShift");
		
		addUniform("sand.heightmap");
		addUniform("sand.displaceScale");
		addUniform("rock.heightmap");
		addUniform("rock.displaceScale");
		addUniform("cliff.heightmap");
		addUniform("cliff.displaceScale");
		
		addUniform("clipplane");
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		Vec2f location = ((TerrainNode) object).getLocation();
		float gap = ((TerrainNode) object).getGap();
		
		setUniform("localMatrix", object.getLocalTransform().getWorldMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i);	
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
		}
		
		for (int i=0; i<4; i++)
		{
			glActiveTexture(GL_TEXTURE22 + i);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i +"].normalmap", 22+i);	
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
		}

		glActiveTexture(GL_TEXTURE1);
		terrConfig.getMaterial1().getDisplacemap().bind();
		setUniformi("sand.heightmap", 1);
		setUniformf("sand.displaceScale", terrConfig.getMaterial1().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE2);
		terrConfig.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.heightmap", 2);
		setUniformf("rock.displaceScale", terrConfig.getMaterial2().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE3);
		terrConfig.getMaterial3().getDisplacemap().bind();
		setUniformi("cliff.heightmap", 3);
		setUniformf("cliff.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
		
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformi("bezier", terrConfig.getBezier());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniform("location", location);
		setUniformf("gap", gap);
		setUniformi("waterReflectionShift", terrConfig.getWaterReflectionShift());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
	}
}