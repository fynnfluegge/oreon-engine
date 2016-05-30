package engine.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE16;
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

public class TerrainFractalTessellation extends Shader{
	
	private static TerrainFractalTessellation instance = null;
	
	public static TerrainFractalTessellation getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainFractalTessellation();
	    }
	      return instance;
	}
	
	protected TerrainFractalTessellation()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("terrain/fractalTerrain/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/fractalTerrain/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/fractalTerrain/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/fractalTerrain/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/fractalTerrain/tessellation/Fragment.glsl"));
		compileShader();
		
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		addUniform("eyePosition");
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
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
			
			addUniform("fractals1[" + i + "].heightmap");
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
			
			addUniform("fractals2[" + i + "].normalmap");
			addUniform("fractals2[" + i + "].scaling");
		}
		
		addUniform("lod1_morph_area");
		addUniform("lod2_morph_area");
		addUniform("lod3_morph_area");
		addUniform("lod4_morph_area");
		addUniform("lod5_morph_area");
		addUniform("lod6_morph_area");
		addUniform("lod7_morph_area");
		addUniform("lod8_morph_area");
		
		addUniform("sunlight.intensity");
		addUniform("sunlight.color");
		addUniform("sunlight.direction");
		addUniform("sunlight.ambient");
		
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
		
		setUniform("sunlight.ambient", RenderingEngine.getDirectionalLight().getAmbient());
		setUniformf("sunlight.intensity", RenderingEngine.getDirectionalLight().getIntensity());
		setUniform("sunlight.color", RenderingEngine.getDirectionalLight().getColor());
		setUniform("sunlight.direction", RenderingEngine.getDirectionalLight().getDirection());	
		
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
		float gap = ((TerrainPatch) object).getGap();
		Vec2f location = ((TerrainPatch) object).getLocation();
		
		for (int i=0; i<10; i++)
		{
			glActiveTexture(GL_TEXTURE15 + i*2);
			terrConfig.getFractals().get(i).getHeightmap().bind();
			setUniformi("fractals0[" + i +"].heightmap", 15+i*2);
			setUniformi("fractals1[" + i +"].heightmap", 15+i*2);
			
			glActiveTexture(GL_TEXTURE16 + i*2);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals1[" + i + "].normalmap", 16+i*2);
			setUniformi("fractals2[" + i + "].normalmap", 16+i*2);
			
			setUniformi("fractals0[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformi("fractals1[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			setUniformi("fractals2[" + i +"].scaling", terrConfig.getFractals().get(i).getScaling());
			
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
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		
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
