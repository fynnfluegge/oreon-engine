package cdk.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE16;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.terrain.TerrainConfiguration;
import modules.terrain.TerrainNode;
import engine.core.Constants;
import engine.core.ResourceLoader;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.shaders.Shader;

public class TerrainShadowShader extends Shader{
	
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
		
		addVertexShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("cdk/terrainEditor/TerrainShadow_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("cdk/terrainEditor/TerrainGrid_FS.glsl"));
		compileShader();

		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
			
			addUniform("fractals1[" + i + "].heightmap");
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailedRange");
		addUniform("lod");
		addUniform("index");
		addUniform("location");
		addUniform("gap");
		
		addUniform("splatmap");
		addUniform("rockgrass.displacemap");
		addUniform("rockgrass.displaceScale");
		addUniform("rock.displacemap");
		addUniform("rock.displaceScale");
		addUniform("snow.displacemap");
		addUniform("snow.displaceScale");
		
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

		setUniform("worldMatrix", object.getTransform().getWorldMatrix());

		TerrainNode terrainNode = (TerrainNode) object;
		TerrainConfiguration terrConfig = terrainNode.getTerrConfig();
		int lod = terrainNode.getLod();
		Vec2f index = terrainNode.getIndex();
		Vec2f location = terrainNode.getLocation();
		float gap = terrainNode.getGap();
		
		for (int i=0; i<10; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i*2);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i*2);
			setUniformi("fractals1[" + i +"].heightmap", 15+i*2);
			
			glActiveTexture(GL_TEXTURE16 + i*2);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i + "].normalmap", 16+i*2);
			
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
		}
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformi("bezier", terrConfig.getBezíer());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniform("location", location);
		setUniformf("gap", gap);
		
		glActiveTexture(GL_TEXTURE3);
		terrConfig.getMaterial1().getDisplacemap().bind();
		setUniformi("rockgrass.displacemap", 3);
		setUniformf("rockgrass.displaceScale", terrConfig.getMaterial1().getDisplaceScale());

		glActiveTexture(GL_TEXTURE4);
		terrConfig.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.displacemap", 4);
		setUniformf("rock.displaceScale", terrConfig.getMaterial2().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE5);
		terrConfig.getMaterial3().getDisplacemap().bind();
		setUniformi("snow.displacemap", 5);
		setUniformf("snow.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
	}


}
