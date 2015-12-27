package engine.shaderprograms.terrain;

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
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.GameObject;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.renderer.terrain.TerrainObject;
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

		addVertexShader(ResourceLoader.loadShader("terrain/tessellation/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/tessellation/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/tessellation/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/tessellation/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/tessellation/Fragment.glsl"));
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
		addUniform("detailRange");
		addUniform("texDetail");
		
		addUniform("heightmap");
		addUniform("normalmap");
		addUniform("occMap");
		addUniform("splatmap");
		
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
		TerrainObject terrain = (TerrainObject) object;
		
		glActiveTexture(GL_TEXTURE0);
		terrain.getHeightmap().bind();
		setUniformi("heightmap", 0);
		glActiveTexture(GL_TEXTURE1);
		terrain.getNormalmap().bind();
		setUniformi("normalmap", 1);
		glActiveTexture(GL_TEXTURE2);
		terrain.getAmbientmap().bind();
		setUniformi("occMap", 2);
		glActiveTexture(GL_TEXTURE3);
		terrain.getSplatmap().bind();
		setUniformi("splatmap", 3);
		
		setUniformf("scaleY", terrain.getScaleY());
		setUniformf("scaleXZ", terrain.getScaleXZ());
		setUniformf("sightRangeFactor", terrain.getSightRangeFactor());
		setUniformi("bezier", terrain.getBezíer());
		setUniformi("tessFactor", terrain.getTessellationFactor());
		setUniformf("tessSlope", terrain.getTessellationSlope());
		setUniformf("tessShift", terrain.getTessellationShift());
		setUniformi("detailRange", terrain.getDetailRange());
		setUniformf("texDetail", terrain.getTexDetail());
		
		glActiveTexture(GL_TEXTURE4);
		terrain.getMaterial1().getDiffusemap().bind();
		setUniformi("sand.diffusemap", 4);
		glActiveTexture(GL_TEXTURE5);
		terrain.getMaterial1().getNormalmap().bind();
		setUniformi("sand.normalmap", 5);
		glActiveTexture(GL_TEXTURE6);
		terrain.getMaterial1().getDisplacemap().bind();
		setUniformi("sand.displacemap", 6);
		
		setUniformf("sand.displaceScale", terrain.getMaterial1().getDisplaceScale());
		setUniformf("sand.shininess", terrain.getMaterial1().getShininess());
		setUniformf("sand.emission", terrain.getMaterial1().getEmission());
		
		glActiveTexture(GL_TEXTURE8);
		terrain.getMaterial2().getDiffusemap().bind();
		setUniformi("rock.diffusemap", 8);
		glActiveTexture(GL_TEXTURE9);
		terrain.getMaterial2().getNormalmap().bind();
		setUniformi("rock.normalmap", 9);
		glActiveTexture(GL_TEXTURE10);
		terrain.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.displacemap", 10);
		
		setUniformf("rock.displaceScale", terrain.getMaterial2().getDisplaceScale());
		setUniformf("rock.shininess", terrain.getMaterial2().getShininess());
		setUniformf("rock.emission", terrain.getMaterial2().getEmission());
		
		glActiveTexture(GL_TEXTURE12);
		terrain.getMaterial3().getDiffusemap().bind();
		setUniformi("snow.diffusemap", 12);
		glActiveTexture(GL_TEXTURE13);
		terrain.getMaterial3().getNormalmap().bind();
		setUniformi("snow.normalmap", 13);
		glActiveTexture(GL_TEXTURE14);
		terrain.getMaterial3().getDisplacemap().bind();
		setUniformi("snow.displacemap", 14);
		
		setUniformf("snow.displaceScale", terrain.getMaterial3().getDisplaceScale());
		setUniformf("snow.shininess", terrain.getMaterial3().getShininess());
		setUniformf("snow.emission", terrain.getMaterial3().getEmission());
	}
}

