package apps.worldgenerator.tools.terrainEditor;

import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE11;
import static org.lwjgl.opengl.GL13.GL_TEXTURE12;
import static org.lwjgl.opengl.GL13.GL_TEXTURE13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE22;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.terrain.TerrainConfiguration;
import modules.terrain.TerrainNode;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.Constants;
import engine.utils.ResourceLoader;

public class TerrainShader extends Shader{
	
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

		addVertexShader(ResourceLoader.loadShader("editor/terrainEditor/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("editor/terrainEditor/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("editor/terrainEditor/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("editor/terrainEditor/Terrain_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("editor/terrainEditor/Terrain_FS.glsl"));
		compileShader();
		
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		addUniform("sightRangeFactor");
		
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
		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
		}
		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}

		addUniform("grass.diffusemap");
		addUniform("grass.shininess");
		addUniform("grass.emission");
		addUniform("sand.diffusemap");
		addUniform("sand.normalmap");
		addUniform("sand.shininess");
		addUniform("sand.emission");
		addUniform("rock.diffusemap");
		addUniform("rock.normalmap");
		addUniform("rock.shininess");
		addUniform("rock.emission");
		addUniform("snow.diffusemap");
		addUniform("snow.normalmap");
		addUniform("snow.shininess");
		addUniform("snow.emission");
		
		addUniform("sand.heightmap");
		addUniform("sand.displaceScale");
		addUniform("rock.heightmap");
		addUniform("rock.displaceScale");
		addUniform("snow.heightmap");
		addUniform("snow.displaceScale");
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		float gap = ((TerrainNode) object).getGap();
		Vec2f location = ((TerrainNode) object).getLocation();
		
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i);	
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
		}
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE22 + i);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i +"].normalmap", 22+i);	
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
		}
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformf("sightRangeFactor", terrConfig.getSightRangeFactor());
		setUniformi("bezier", terrConfig.getBezíer());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
		
		glActiveTexture(GL_TEXTURE4);
		terrConfig.getMaterial0().getDiffusemap().bind();
		setUniformi("grass.diffusemap", 4);
		
		setUniformf("grass.shininess", terrConfig.getMaterial0().getShininess());
		setUniformf("grass.emission", terrConfig.getMaterial0().getEmission());
		
		glActiveTexture(GL_TEXTURE6);
		terrConfig.getMaterial1().getDiffusemap().bind();
		setUniformi("sand.diffusemap", 6);
		glActiveTexture(GL_TEXTURE7);
		terrConfig.getMaterial1().getNormalmap().bind();
		setUniformi("sand.normalmap", 7);

		setUniformf("sand.shininess", terrConfig.getMaterial1().getShininess());
		setUniformf("sand.emission", terrConfig.getMaterial1().getEmission());
		
		glActiveTexture(GL_TEXTURE8);
		terrConfig.getMaterial2().getDiffusemap().bind();
		setUniformi("rock.diffusemap", 8);
		glActiveTexture(GL_TEXTURE9);
		terrConfig.getMaterial2().getNormalmap().bind();
		setUniformi("rock.normalmap", 9);

		setUniformf("rock.shininess", terrConfig.getMaterial2().getShininess());
		setUniformf("rock.emission", terrConfig.getMaterial2().getEmission());
		
		glActiveTexture(GL_TEXTURE10);
		terrConfig.getMaterial3().getDiffusemap().bind();
		setUniformi("snow.diffusemap", 10);
		glActiveTexture(GL_TEXTURE11);
		terrConfig.getMaterial3().getNormalmap().bind();
		setUniformi("snow.normalmap", 11);

		setUniformf("snow.shininess", terrConfig.getMaterial3().getShininess());
		setUniformf("snow.emission", terrConfig.getMaterial3().getEmission());
		
		glActiveTexture(GL_TEXTURE12);
		terrConfig.getMaterial1().getDisplacemap().bind();
		setUniformi("sand.heightmap", 12);
		setUniformf("sand.displaceScale", terrConfig.getMaterial1().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE13);
		terrConfig.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.heightmap", 13);
		setUniformf("rock.displaceScale", terrConfig.getMaterial2().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE14);
		terrConfig.getMaterial3().getDisplacemap().bind();
		setUniformi("snow.heightmap", 14);
		setUniformf("snow.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
	}
}
