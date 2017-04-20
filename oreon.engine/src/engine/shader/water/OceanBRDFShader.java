package engine.shader.water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.lighting.DirectionalLight;
import modules.water.Water;
import engine.core.Camera;
import engine.core.Window;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class OceanBRDFShader extends Shader{

	private static OceanBRDFShader instance = null;

	public static OceanBRDFShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new OceanBRDFShader();
	    }
	      return instance;
	}
	
	protected OceanBRDFShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/ocean/Ocean_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/ocean/Ocean_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/ocean/Ocean_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/ocean/Ocean_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/ocean/Ocean_FS.glsl"));
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
	
	public void updateUniforms(GameObject object)
	{
		setUniform("projectionViewMatrix", Camera.getInstance().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
				
		setUniform("eyePosition", Camera.getInstance().getPosition());
		setUniformi("windowWidth", Window.getInstance().getWidth());
		setUniformi("windowHeight", Window.getInstance().getHeight());
		
		setUniform("sunlight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("sunlight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("sunlight.color", DirectionalLight.getInstance().getColor());
		setUniform("sunlight.direction", DirectionalLight.getInstance().getDirection());	
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
		
		Water ocean = (Water) object;
		
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
		ocean.getNormalmapRenderer().getNormalmap().bind();
		setUniformi("normalmap",  3);
		glActiveTexture(GL_TEXTURE4);
		ocean.getFft().getDy().bind();
		setUniformi("Dy", 4);
		glActiveTexture(GL_TEXTURE5);
		ocean.getFft().getDx().bind();
		setUniformi("Dx", 5);
		glActiveTexture(GL_TEXTURE6);
		ocean.getFft().getDz().bind();
		setUniformi("Dz", 6);
	}
}
