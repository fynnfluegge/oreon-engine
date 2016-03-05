package engine.shaderprograms.terrain.specificHeightmap;

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
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.GameObject;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.math.Vec2f;
import engine.renderer.terrain.TerrainConfiguration;
import engine.renderer.terrain.TerrainPatch;
import engine.shaderprograms.Shader;

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

		addVertexShader(ResourceLoader.loadShader("terrain/fromHeightmap/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/fromHeightmap/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/fromHeightmap/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/fromHeightmap/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/fromHeightmap/tessellation/Fragment.glsl"));
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
		
		addUniform("heightmap");
		addUniform("normalmap");
		addUniform("occMap");
		addUniform("splatmap");
		
		for (int i=0; i<10; i++)
		{
			addUniform("fractals[" + i + "].heightmap");
			addUniform("fractals[" + i + "].normalmap");
		}
		
		addUniform("sunlight.intensity");
		addUniform("sunlight.color");
		addUniform("sunlight.direction");
		addUniform("sunlight.ambient");
		
		addUniform("sand.diffusemap");
		addUniform("sand.normalmap");
		addUniform("sand.displacemap");
		addUniform("sand.displaceScale");
		addUniform("sand.shininess");
		addUniform("sand.emission");
		addUniform("rock.diffusemap");
		addUniform("rock.normalmap");
		addUniform("rock.displacemap");
		addUniform("rock.displaceScale");
		addUniform("rock.shininess");
		addUniform("rock.emission");
		addUniform("snow.diffusemap");
		addUniform("snow.normalmap");
		addUniform("snow.displacemap");
		addUniform("snow.displaceScale");
		addUniform("snow.shininess");
		addUniform("snow.emission");
		
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
			setUniformi("fractals[" + i +"].heightmap", 15+i*2);
			
			glActiveTexture(GL_TEXTURE16 + i*2);
			terrConfig.getFractals().get(i).getNormalmap().bind();
			setUniformi("fractals[" + i + "].normalmap", 16+i*2);
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
		
		glActiveTexture(GL_TEXTURE4);
		terrConfig.getMaterial1().getDiffusemap().bind();
		setUniformi("sand.diffusemap", 4);
		glActiveTexture(GL_TEXTURE5);
		terrConfig.getMaterial1().getNormalmap().bind();
		setUniformi("sand.normalmap", 5);
		glActiveTexture(GL_TEXTURE6);
		terrConfig.getMaterial1().getDisplacemap().bind();
		setUniformi("sand.displacemap", 6);
		
		setUniformf("sand.displaceScale", terrConfig.getMaterial1().getDisplaceScale());
		setUniformf("sand.shininess", terrConfig.getMaterial1().getShininess());
		setUniformf("sand.emission", terrConfig.getMaterial1().getEmission());
		
		glActiveTexture(GL_TEXTURE8);
		terrConfig.getMaterial2().getDiffusemap().bind();
		setUniformi("rock.diffusemap", 8);
		glActiveTexture(GL_TEXTURE9);
		terrConfig.getMaterial2().getNormalmap().bind();
		setUniformi("rock.normalmap", 9);
		glActiveTexture(GL_TEXTURE10);
		terrConfig.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.displacemap", 10);
		
		setUniformf("rock.displaceScale", terrConfig.getMaterial2().getDisplaceScale());
		setUniformf("rock.shininess", terrConfig.getMaterial2().getShininess());
		setUniformf("rock.emission", terrConfig.getMaterial2().getEmission());
		
		glActiveTexture(GL_TEXTURE12);
		terrConfig.getMaterial3().getDiffusemap().bind();
		setUniformi("snow.diffusemap", 12);
		glActiveTexture(GL_TEXTURE13);
		terrConfig.getMaterial3().getNormalmap().bind();
		setUniformi("snow.normalmap", 13);
		glActiveTexture(GL_TEXTURE14);
		terrConfig.getMaterial3().getDisplacemap().bind();
		setUniformi("snow.displacemap", 14);
		
		setUniformf("snow.displaceScale", terrConfig.getMaterial3().getDisplaceScale());
		setUniformf("snow.shininess", terrConfig.getMaterial3().getShininess());
		setUniformf("snow.emission", terrConfig.getMaterial3().getEmission());
	}
}

