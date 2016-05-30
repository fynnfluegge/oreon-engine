package engine.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE15;
import static org.lwjgl.opengl.GL13.GL_TEXTURE16;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
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

public class TerrainGrid extends Shader{
	
	private static TerrainGrid instance = null;
	
	public static TerrainGrid getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainGrid();
	    }
	      return instance;
	}
	
	protected TerrainGrid()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("terrain/heightmap/grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/heightmap/grid/Tessellation COntrol.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/heightmap/grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/heightmap/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/heightmap/grid/Fragment.glsl"));
		compileShader();
		
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		addUniform("eyePosition");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
			
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
		}
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailedRange");
		addUniform("texDetail");
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
		
		addUniform("heightmap");
		addUniform("normalmap");
		addUniform("splatmap");
		addUniform("rockgrass.displacemap");
		addUniform("rockgrass.displaceScale");
		addUniform("rock.displacemap");
		addUniform("rock.displaceScale");
		addUniform("snow.displacemap");
		addUniform("snow.displaceScale");
		
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
		
		glActiveTexture(GL_TEXTURE0);
		terrConfig.getHeightmap().bind();
		setUniformi("heightmap", 0);
		glActiveTexture(GL_TEXTURE1);
		terrConfig.getNormalmap().bind();
		setUniformi("normalmap", 1);
		glActiveTexture(GL_TEXTURE2);
		terrConfig.getSplatmap().bind();
		setUniformi("splatmap", 2);
		
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
		setUniformi("bezier", terrConfig.getBezíer());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailedRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
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
	}
}


