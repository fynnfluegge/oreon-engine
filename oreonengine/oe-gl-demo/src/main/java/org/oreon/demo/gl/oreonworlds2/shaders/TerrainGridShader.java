package org.oreon.demo.gl.oreonworlds2.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE12;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
import static org.lwjgl.opengl.GL13.GL_TEXTURE16;
import static org.lwjgl.opengl.GL13.GL_TEXTURE18;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.gl.terrain.TerrainConfiguration;
import org.oreon.modules.gl.terrain.TerrainNode;

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

		addVertexShader(ResourceLoader.loadShader("oreonworlds2/shaders/Terrain_Shader/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds2/shaders/Terrain_Shader/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds2/shaders/Terrain_Shader/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds2/shaders/Terrain_Shader/TerrainGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds2/shaders/Terrain_Shader/TerrainGrid_FS.glsl"));
		compileShader();
		
		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailedRange");
		addUniform("index");
		addUniform("gap");
		addUniform("lod");
		addUniform("location");
		addUniform("texDetail");
		addUniform("waterReflectionShift");
//		addUniform("isCameraUnderWater");
		
//		addUniform("caustics");
//		addUniform("dudvCaustics");
//		addUniform("distortionCaustics");
		
		addUniform("heightmap");
		
//		for (int i=0; i<1; i++)
//		{
//			addUniform("fractals1[" + i + "].normalmap");
//			addUniform("fractals1[" + i + "].scaling");
//		}
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniform("sand.splatmap");
		addUniform("sand.heightmap");
		addUniform("sand.displaceScale");
		
		addUniform("rock.splatmap");
		addUniform("rock.heightmap");
		addUniform("rock.displaceScale");
		
		addUniform("cliff.splatmap");
		addUniform("cliff.heightmap");
		addUniform("cliff.displaceScale");
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
//		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderingEngine().isCameraUnderWater() ? 1 : 0);		
		
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
		
//		for (int i=0; i<1; i++)
//		{
//			glActiveTexture(GL_TEXTURE28 + i);
//			terrConfig.getFractals().get(i+6).getNormalmap().bind();
//			setUniformi("fractals1[" + i +"].normalmap", 28+i);	
//			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i+6).getScaling());
//		}
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformi("bezier", terrConfig.getBezier());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		setUniformi("waterReflectionShift", terrConfig.getWaterReflectionShift());
		
//		glActiveTexture(GL_TEXTURE2);
//		UnderWater.getInstance().getCausticsMap().bind();
//		setUniformi("caustics", 2);
//		glActiveTexture(GL_TEXTURE3);
//		UnderWater.getInstance().getDudvMap().bind();
//		setUniformi("dudvCaustics", 3);
		
//		setUniformf("distortionCaustics", UnderWater.getInstance().getDistortion());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
		
		// sand material
		glActiveTexture(GL_TEXTURE8);
		terrConfig.getSplatmaps().get(1).bind();
		setUniformi("sand.splatmap", 8);
		
		glActiveTexture(GL_TEXTURE10);
		terrConfig.getMaterial1().getHeightmap().bind();
		setUniformi("sand.heightmap", 10);
		setUniformf("sand.displaceScale", terrConfig.getMaterial1().getDisplacementScale());
		
		// rock material
		glActiveTexture(GL_TEXTURE12);
		terrConfig.getSplatmaps().get(2).bind();
		setUniformi("rock.splatmap", 12);
		
		glActiveTexture(GL_TEXTURE14);
		terrConfig.getMaterial2().getHeightmap().bind();
		setUniformi("rock.heightmap", 14);
		setUniformf("rock.displaceScale", terrConfig.getMaterial2().getDisplacementScale());
		
		// cliff material
		glActiveTexture(GL_TEXTURE16);
		terrConfig.getSplatmaps().get(3).bind();
		setUniformi("cliff.splatmap", 16);
		
		glActiveTexture(GL_TEXTURE18);
		terrConfig.getMaterial3().getHeightmap().bind();
		setUniformi("cliff.heightmap", 18);
		setUniformf("cliff.displaceScale", terrConfig.getMaterial3().getDisplacementScale());
	}

}
