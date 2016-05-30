package engine.shaders.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE12;
import static org.lwjgl.opengl.GL13.GL_TEXTURE13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE14;
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

public class TerrainTessellation extends Shader{
	
	private static TerrainTessellation instance = null;
	
	public static TerrainTessellation getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainTessellation();
	    }
	      return instance;
	}
	
	protected TerrainTessellation()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("terrain/heightmap/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/heightmap/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/heightmap/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/heightmap/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/heightmap/tessellation/Fragment.glsl"));
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
		addUniform("texDetail");
		addUniform("index");
		addUniform("gap");
		addUniform("lod");
		addUniform("location");
		
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
		addUniform("occMap");
		addUniform("splatmap");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals0[" + i + "].heightmap");
			addUniform("fractals0[" + i + "].scaling");
			addUniform("fractals0[" + i + "].strength");
			
			addUniform("fractals1[" + i + "].normalmap");
			addUniform("fractals1[" + i + "].scaling");
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
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
		
		addUniform("numLights");
		
		for (int i=0; i<10; i++)
		{
			addUniform("lights[" + i + "].isEnabled");
			addUniform("lights[" + i + "].isSpot");
			addUniform("lights[" + i + "].position");
			addUniform("lights[" + i + "].color");
			addUniform("lights[" + i + "].ambient");
			addUniform("lights[" + i + "].intensity");
			addUniform("lights[" + i + "].ConstantAttenuation");
			addUniform("lights[" + i + "].LinearAttenuation");
			addUniform("lights[" + i + "].QuadraticAttenuation");
			addUniform("lights[" + i + "].ConeDirection");
			addUniform("lights[" + i + "].SpotCosCutoff");
			addUniform("lights[" + i + "].SpotExponent");
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
		
		setUniformi("numLights", RenderingEngine.getLights().size());
		
		for (int i=0; i<RenderingEngine.getLights().size(); i++)
		{
			setUniformi("lights[" + i + "].isEnabled", RenderingEngine.getLights().get(i).isEnabled());
			setUniformi("lights[" + i + "].isSpot", RenderingEngine.getLights().get(i).isSpot());
			setUniform("lights[" + i + "].position", RenderingEngine.getLights().get(i).getPosition());
			setUniform("lights[" + i + "].color", RenderingEngine.getLights().get(i).getColor());
			setUniform("lights[" + i + "].ambient", RenderingEngine.getLights().get(i).getAmbient());
			setUniformf("lights[" + i + "].intensity", RenderingEngine.getLights().get(i).getIntensity());
			setUniformf("lights[" + i + "].ConstantAttenuation", RenderingEngine.getLights().get(i).getConstantAttenuation());
			setUniformf("lights[" + i + "].LinearAttenuation", RenderingEngine.getLights().get(i).getLinearAttenuation());
			setUniformf("lights[" + i + "].QuadraticAttenuation", RenderingEngine.getLights().get(i).getQuadraticAttenuation());
			setUniform("lights[" + i + "].ConeDirection", RenderingEngine.getLights().get(i).getConeDirection());
			setUniformf("lights[" + i + "].SpotCosCutoff", RenderingEngine.getLights().get(i).getSpotCosCutoff());
			setUniformf("lights[" + i + "].SpotExponent", RenderingEngine.getLights().get(i).getSpotExponent());
		}
	}
	
	public void sendUniforms(GameObject object)
	{	
		TerrainConfiguration terrConfig = ((TerrainPatch) object).getTerrConfig();
		int lod = ((TerrainPatch) object).getLod();
		Vec2f index = ((TerrainPatch) object).getIndex();
		float gap = ((TerrainPatch) object).getGap();
		Vec2f location = ((TerrainPatch) object).getLocation();
		
		glActiveTexture(GL_TEXTURE0);
		terrConfig.getHeightmap().bind();
		setUniformi("heightmap", 0);
		glActiveTexture(GL_TEXTURE1);
		terrConfig.getNormalmap().bind();
		setUniformi("normalmap", 1);
		glActiveTexture(GL_TEXTURE2);
		terrConfig.getAmbientmap().bind();
		setUniformi("occMap", 2);
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
		
		setUniformi("lod1_morph_area", terrConfig.getLod1_morphing_area());
		setUniformi("lod2_morph_area", terrConfig.getLod2_morphing_area());
		setUniformi("lod3_morph_area", terrConfig.getLod3_morphing_area());
		setUniformi("lod4_morph_area", terrConfig.getLod4_morphing_area());
		setUniformi("lod5_morph_area", terrConfig.getLod5_morphing_area());
		setUniformi("lod6_morph_area", terrConfig.getLod6_morphing_area());
		setUniformi("lod7_morph_area", terrConfig.getLod7_morphing_area());
		setUniformi("lod8_morph_area", terrConfig.getLod8_morphing_area());
		
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

