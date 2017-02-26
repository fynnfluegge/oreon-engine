package apps.samples.fractalworlds;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE22;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
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

		addVertexShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_FS.glsl"));
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
		
		for (int i=0; i<7; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
		}
		
		for (int i=0; i<10; i++){
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		for (int i=0; i<8; i++){
			addUniform("lod_morph_area[" + i + "]");
		}
		
		addUniform("shadowMaps");
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("LightViewProjections");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);

		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		TerrainConfiguration terrConfig = ((TerrainNode) object).getTerrConfig();
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		float gap = ((TerrainNode) object).getGap();
		Vec2f location = ((TerrainNode) object).getLocation();
		
		for (int i=0; i<7; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i);
			
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals0[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
		}
		
		for (int i=0; i<10; i++)
		{
			glActiveTexture(GL_TEXTURE22 + i);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i +"].normalmap", 22+i);
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
		}
		
		glActiveTexture(GL_TEXTURE0);
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		setUniformi("shadowMaps", 0);
		
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformf("sightRangeFactor", terrConfig.getSightRangeFactor());
		setUniformi("bezier", terrConfig.getBezíer());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
	}
}
