package engine.shaderprograms.water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.core.Window;
import engine.gameObject.GameObject;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.models.data.Material;
import engine.renderer.water.WaterSurface;
import engine.shaderprograms.Shader;

public class OceanBRDF extends Shader{

private static OceanBRDF instance = null;
	
	private float distortion;

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
		setUniformi("windowWidth", Window.getWidth());
		setUniformi("windowHeight", Window.getHeight());
		
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
		setUniformf("distortion", distortion);
		setUniformf("kReflection", ocean.getkReflection());
		setUniformf("kRefraction", ocean.getkRefraction());
		setUniformf("emission", ocean.getEmission());
		setUniformf("shininess", ocean.getShininess());
		
		glActiveTexture(GL_TEXTURE1);
		ocean.getReflectionTexture().bind();
		setUniformi("waterReflection", 1);
		glActiveTexture(GL_TEXTURE2);
		ocean.getRefractionTexture().bind();
		setUniformi("waterRefraction", 2);
		glActiveTexture(GL_TEXTURE3);
		ocean.getFFT().getNormalmap().bind();
		setUniformi("normalmap",  3);
		glActiveTexture(GL_TEXTURE4);
		ocean.getFFT().getDy().bind();
		setUniformi("Dy", 4);
		glActiveTexture(GL_TEXTURE5);
		ocean.getFFT().getDx().bind();
		setUniformi("Dx", 5);
		glActiveTexture(GL_TEXTURE6);
		ocean.getFFT().getDz().bind();
		setUniformi("Dz", 6);
	}
	
	public void sendUniforms(Material material)
	{	
		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("dudv", 0);
	}

	public float getDistortion() {
		return distortion;
	}

	public void setDistortion(float distortion) {
		this.distortion = distortion;
	}
}
