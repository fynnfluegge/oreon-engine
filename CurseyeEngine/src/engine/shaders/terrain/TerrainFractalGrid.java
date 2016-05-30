package engine.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.terrain.TerrainConfiguration;
import modules.terrain.TerrainPatch;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.shaders.Shader;

public class TerrainFractalGrid extends Shader{
	
	private static TerrainFractalGrid instance = null;
	
	public static TerrainFractalGrid getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainFractalGrid();
	    }
	      return instance;
	}
	
	protected TerrainFractalGrid()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("terrain/FractalTerrain/grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/FractalTerrain/grid/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/FractalTerrain/grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/FractalTerrain/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/FractalTerrain/grid/Fragment.glsl"));
		compileShader();
		
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		addUniform("eyePosition");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals[" + i + "].heightmap");
			addUniform("fractals[" + i + "].normalmap");
			addUniform("fractals[" + i + "].scaling");
			addUniform("fractals[" + i + "].strength");
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
		
		addUniform("lod1_morph_area");
		addUniform("lod2_morph_area");
		addUniform("lod3_morph_area");
		addUniform("lod4_morph_area");
		addUniform("lod5_morph_area");
		addUniform("lod6_morph_area");
		addUniform("lod7_morph_area");
		addUniform("lod8_morph_area");
		
		addUniform("clipplane");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionViewMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("projectionViewMatrix", projectionViewMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(GameObject object)
	{
		TerrainConfiguration terrConfig = ((TerrainPatch) object).getTerrConfig();
		int lod = ((TerrainPatch) object).getLod();
		Vec2f index = ((TerrainPatch) object).getIndex();
		Vec2f location = ((TerrainPatch) object).getLocation();
		float gap = ((TerrainPatch) object).getGap();
		
		for (int i=0; i<10; i++)
		{
			glActiveTexture(GL_TEXTURE0 + i*2);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals[" + i +"].heightmap", 0+i*2);
			
			glActiveTexture(GL_TEXTURE1 + i*2);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals[" + i + "].normalmap", 1+i*2);
			
			setUniformi("fractals[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformf("fractals[" + i +"].strength", terrConfig.getFractals().get(i).getStrength());
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
		
		setUniformi("lod1_morph_area", terrConfig.getLod1_morphing_area());
		setUniformi("lod2_morph_area", terrConfig.getLod2_morphing_area());
		setUniformi("lod3_morph_area", terrConfig.getLod3_morphing_area());
		setUniformi("lod4_morph_area", terrConfig.getLod4_morphing_area());
		setUniformi("lod5_morph_area", terrConfig.getLod5_morphing_area());
		setUniformi("lod6_morph_area", terrConfig.getLod6_morphing_area());
		setUniformi("lod7_morph_area", terrConfig.getLod7_morphing_area());
		setUniformi("lod8_morph_area", terrConfig.getLod8_morphing_area());
	}
}