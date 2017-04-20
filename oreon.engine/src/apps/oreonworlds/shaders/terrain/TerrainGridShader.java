package apps.oreonworlds.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.terrain.TerrainConfiguration;
import modules.terrain.TerrainNode;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class TerrainGridShader extends Shader{
	
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

		addVertexShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/terrain/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/terrain/TerrainGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/terrain/TerrainGrid_FS.glsl"));
		compileShader();
		
		addUniform("worldMatrix");
		addUniform("scaleY");

		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
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
		addUniform("snow.heightmap");
		addUniform("snow.displaceScale");
		
		addUniform("clipplane");
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		Vec2f location = ((TerrainNode) object).getLocation();
		float gap = ((TerrainNode) object).getGap();
		
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i);	
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
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
		setUniformi("snow.heightmap", 3);
		setUniformf("snow.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
		
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformi("bezier", terrConfig.getBezíer());
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