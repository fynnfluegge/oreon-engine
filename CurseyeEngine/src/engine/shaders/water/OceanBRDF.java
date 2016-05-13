package engine.shaders.water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.water.WaterSurface;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.core.OpenGLWindow;
import engine.gameObject.GameObject;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.shaders.Shader;

public class OceanBRDF extends Shader{

private static OceanBRDF instance = null;

	public static OceanBRDF getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new OceanBRDF();
	    }
	      return instance;
	}
	
	protected OceanBRDF()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("ocean/brdf/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("ocean/brdf/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("ocean/brdf/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("ocean/brdf/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("ocean/brdf/Fragment.glsl"));
		compileShader();
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		addUniform("windowWidth");
		addUniform("windowHeight");
		
		addUniform("waterReflection");
		addUniform("waterRefraction");
		addUniform("dudv");
		addUniform("displacementScale");
		addUniform("choppiness");
		addUniform("texDetail");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("distortion");
		addUniform("kReflection");
		addUniform("kRefraction");
		addUniform("largeDetailRange");
		
		addUniform("sunlight.intensity");
		addUniform("sunlight.color");
		addUniform("sunlight.direction");
		addUniform("sunlight.ambient");
		addUniform("emission");
		addUniform("shininess");
		
		addUniform("normalmap");
		
		addUniform("Dy");
		addUniform("Dx");
		addUniform("Dz");
		addUniform("motion");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("projectionViewMatrix", projectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		setUniformi("windowWidth", OpenGLWindow.getWidth());
		setUniformi("windowHeight", OpenGLWindow.getHeight());
		
		setUniform("sunlight.ambient", RenderingEngine.getDirectionalLight().getAmbient());
		setUniformf("sunlight.intensity", RenderingEngine.getDirectionalLight().getIntensity());
		setUniform("sunlight.color", RenderingEngine.getDirectionalLight().getColor());
		setUniform("sunlight.direction", RenderingEngine.getDirectionalLight().getDirection());	
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(GameObject object)
	{
		
		
		WaterSurface ocean = (WaterSurface) object;
		
		setUniformf("displacementScale", ocean.getDisplacementScale());
		setUniformf("choppiness", ocean.getChoppiness());
		setUniformi("texDetail", ocean.getTexDetail());
		setUniformi("tessFactor", ocean.getTessellationFactor());
		setUniformf("tessSlope", ocean.getTessellationSlope());
		setUniformf("tessShift", ocean.getTessellationShift());
		setUniformi("largeDetailRange", ocean.getLargeDetailRange());
		setUniformf("distortion", ocean.getDistorion());
		setUniformf("kReflection", ocean.getkReflection());
		setUniformf("kRefraction", ocean.getkRefraction());
		setUniformf("emission", ocean.getEmission());
		setUniformf("shininess", ocean.getShininess());
		setUniformf("motion", ocean.getMotion());
		
		glActiveTexture(GL_TEXTURE0);
		ocean.getDudv().bind();
		setUniformi("dudv", 0);
		glActiveTexture(GL_TEXTURE1);
		ocean.getReflectionTexture().bind();
		setUniformi("waterReflection", 1);
		glActiveTexture(GL_TEXTURE2);
		ocean.getRefractionTexture().bind();
		setUniformi("waterRefraction", 2);
		glActiveTexture(GL_TEXTURE3);
		ocean.getWaterMaps().getNormalmapRenderer().getNormalmap().bind();
		setUniformi("normalmap",  3);
		glActiveTexture(GL_TEXTURE4);
		ocean.getWaterMaps().getFFT().getDy().bind();
		setUniformi("Dy", 4);
		glActiveTexture(GL_TEXTURE5);
		ocean.getWaterMaps().getFFT().getDx().bind();
		setUniformi("Dx", 5);
		glActiveTexture(GL_TEXTURE6);
		ocean.getWaterMaps().getFFT().getDz().bind();
		setUniformi("Dz", 6);
	}
}
