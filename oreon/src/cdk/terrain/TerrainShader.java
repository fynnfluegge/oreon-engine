package cdk.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE12;
import static org.lwjgl.opengl.GL13.GL_TEXTURE13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE16;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.lighting.DirectionalLight;
import modules.terrain.TerrainConfiguration;
import modules.terrain.TerrainNode;
import engine.core.Constants;
import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.shaders.Shader;

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

		addVertexShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("cdk/terrainEditor/Terrain_FS.glsl"));
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
		
		addUniform("splatmap");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
			
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniform("sunlight.intensity");
		addUniform("sunlight.color");
		addUniform("sunlight.direction");
		addUniform("sunlight.ambient");
		
		addUniform("rock0.displaceScale");
		addUniform("rock0.displacemap");
		addUniform("sand0.displaceScale");
		addUniform("sand0.displacemap");
		addUniform("snow0.displaceScale");
		addUniform("snow0.displacemap");
		
		addUniform("sand1.diffusemap");
		addUniform("sand1.normalmap");
		addUniform("sand1.shininess");
		addUniform("sand1.emission");
		addUniform("rock1.diffusemap");
		addUniform("rock1.normalmap");
		addUniform("rock1.shininess");
		addUniform("rock1.emission");
		addUniform("snow1.diffusemap");
		addUniform("snow1.normalmap");
		addUniform("snow1.shininess");
		addUniform("snow1.emission");
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
	
		setUniform("sunlight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("sunlight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("sunlight.color", DirectionalLight.getInstance().getColor());
		setUniform("sunlight.direction", DirectionalLight.getInstance().getDirection());	
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		float gap = ((TerrainNode) object).getGap();
		Vec2f location = ((TerrainNode) object).getLocation();
		
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		
		glActiveTexture(GL_TEXTURE3);
		terrConfig.getSplatmap().bind();
		setUniformi("splatmap", 3);
		
		for (int i=0; i<10; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i*2);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i*2);
			
			glActiveTexture(GL_TEXTURE16 + i*2);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i + "].normalmap", 16+i*2);
			
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
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
		terrConfig.getMaterial1().getDiffusemap().bind();
		setUniformi("sand1.diffusemap", 4);
		glActiveTexture(GL_TEXTURE5);
		terrConfig.getMaterial1().getNormalmap().bind();
		setUniformi("sand1.normalmap", 5);
		glActiveTexture(GL_TEXTURE6);
		terrConfig.getMaterial1().getDisplacemap().bind();
		setUniformi("sand0.displacemap", 6);
		
		setUniformf("sand0.displaceScale", terrConfig.getMaterial1().getDisplaceScale());
		setUniformf("sand1.shininess", terrConfig.getMaterial1().getShininess());
		setUniformf("sand1.emission", terrConfig.getMaterial1().getEmission());
		
		glActiveTexture(GL_TEXTURE8);
		terrConfig.getMaterial2().getDiffusemap().bind();
		setUniformi("rock1.diffusemap", 8);
		glActiveTexture(GL_TEXTURE9);
		terrConfig.getMaterial2().getNormalmap().bind();
		setUniformi("rock1.normalmap", 9);
		glActiveTexture(GL_TEXTURE10);
		terrConfig.getMaterial2().getDisplacemap().bind();
		setUniformi("rock0.displacemap", 10);
		
		setUniformf("rock0.displaceScale", terrConfig.getMaterial2().getDisplaceScale());
		setUniformf("rock1.shininess", terrConfig.getMaterial2().getShininess());
		setUniformf("rock1.emission", terrConfig.getMaterial2().getEmission());
		
		glActiveTexture(GL_TEXTURE12);
		terrConfig.getMaterial3().getDiffusemap().bind();
		setUniformi("snow1.diffusemap", 12);
		glActiveTexture(GL_TEXTURE13);
		terrConfig.getMaterial3().getNormalmap().bind();
		setUniformi("snow1.normalmap", 13);
		glActiveTexture(GL_TEXTURE14);
		terrConfig.getMaterial3().getDisplacemap().bind();
		setUniformi("snow0.displacemap", 14);
		
		setUniformf("snow0.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
		setUniformf("snow1.shininess", terrConfig.getMaterial3().getShininess());
		setUniformf("snow1.emission", terrConfig.getMaterial3().getEmission());
	}
}
