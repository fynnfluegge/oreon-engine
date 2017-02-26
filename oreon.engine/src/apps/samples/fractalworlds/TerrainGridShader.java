package apps.samples.fractalworlds;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
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

		addVertexShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/Terrain_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/TerrainGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("samples/FractalWorlds/Terrain/shader/TerrainGrid_FS.glsl"));
		compileShader();
		
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
		
		addUniform("clipplane");
		
		addUniformBlock("Camera");
	}
	
	public void updateUniforms(GameObject object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		
		setUniform("clipplane", RenderingEngine.getClipplane());

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
		setUniformi("bezier", terrConfig.getBezíer());
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