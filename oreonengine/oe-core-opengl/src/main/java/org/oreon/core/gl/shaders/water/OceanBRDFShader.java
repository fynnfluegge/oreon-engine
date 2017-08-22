package org.oreon.core.gl.shaders.water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.light.DirectionalLight;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;
import org.oreon.modules.water.UnderWater;
import org.oreon.modules.water.Water;

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
		addUniform("dudvRefracReflec");
		addUniform("dudvCaustics");
		addUniform("caustics");
		addUniform("distortionRefracReflec");
		addUniform("distortionCaustics");
		addUniform("displacementScale");
		addUniform("choppiness");
		addUniform("texDetail");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("kReflection");
		addUniform("kRefraction");
		addUniform("largeDetailRange");
		addUniform("sightRangeFactor");
		
		addUniform("sunlight.intensity");
		addUniform("sunlight.color");
		addUniform("sunlight.direction");
		addUniform("sunlight.ambient");
		addUniform("emission");
		addUniform("shininess");

		addUniform("isCameraUnderWater");
		
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
		setUniform("projectionViewMatrix", CoreSystem.getInstance().getScenegraph().getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
				
		setUniform("eyePosition", CoreSystem.getInstance().getScenegraph().getCamera().getPosition());
		setUniformi("windowWidth", CoreSystem.getInstance().getWindow().getWidth());
		setUniformi("windowHeight", CoreSystem.getInstance().getWindow().getHeight());
		
		setUniform("sunlight.ambient", DirectionalLight.getInstance().getAmbient());
		setUniformf("sunlight.intensity", DirectionalLight.getInstance().getIntensity());
		setUniform("sunlight.color", DirectionalLight.getInstance().getColor());
		setUniform("sunlight.direction", DirectionalLight.getInstance().getDirection());	
		
		setUniformf("sightRangeFactor", CoreSystem.getInstance().getRenderingEngine().getSightRangeFactor());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", CoreSystem.getInstance().getScenegraph().getCamera().getFrustumPlanes()[i]);
		}
		
		Water ocean = (Water) object;
		
		setUniformf("displacementScale", ocean.getDisplacementScale());
		setUniformf("choppiness", ocean.getChoppiness());
		setUniformi("texDetail", ocean.getTexDetail());
		setUniformi("tessFactor", ocean.getTessellationFactor());
		setUniformf("tessSlope", ocean.getTessellationSlope());
		setUniformf("tessShift", ocean.getTessellationShift());
		setUniformi("largeDetailRange", ocean.getLargeDetailRange());
		setUniformf("distortionRefracReflec", ocean.getDistortion());
		setUniformf("distortionCaustics", UnderWater.getInstance().getDistortion());
		setUniformf("kReflection", ocean.getkReflection());
		setUniformf("kRefraction", ocean.getkRefraction());
		setUniformf("emission", ocean.getEmission());
		setUniformf("shininess", ocean.getShininess());
		setUniformf("motion", ocean.getMotion());
		setUniformi("isCameraUnderWater", CoreSystem.getInstance().getRenderingEngine().isCameraUnderWater() ? 1 : 0);
				
		glActiveTexture(GL_TEXTURE0);
		ocean.getDudv().bind();
		setUniformi("dudvRefracReflec", 0);
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
		glActiveTexture(GL_TEXTURE7);
		UnderWater.getInstance().getCausticsMap().bind();
		setUniformi("caustics", 7);
		glActiveTexture(GL_TEXTURE8);
		UnderWater.getInstance().getDudvMap().bind();
		setUniformi("dudvCaustics", 8);
	}
}
