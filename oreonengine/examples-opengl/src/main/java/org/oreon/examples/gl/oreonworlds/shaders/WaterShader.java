package org.oreon.examples.gl.oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.gl.components.water.Water;

public class WaterShader extends GLShaderProgram{

	private static WaterShader instance = null;

	public static WaterShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new WaterShader();
	    }
	      return instance;
	}
	
	protected WaterShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/water/water.vert"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/water/water.tesc"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/water/water.tese"));
		addGeometryShader(ResourceLoader.loadShader("shaders/water/water.geom"));
		addFragmentShader(ResourceLoader.loadShader("shaders/water/water.frag"));
		compileShader();
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		addUniform("windowWidth");
		addUniform("windowHeight");
		
		addUniform("waterReflection");
		addUniform("kReflection");
		addUniform("waterRefraction");
		addUniform("kRefraction");
		addUniform("dudvMap");
		addUniform("distortion");
		addUniform("displacementScale");
		addUniform("choppiness");
		addUniform("texDetail");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailRange");
		addUniform("fresnelFactor");
		addUniform("reflectionBlendFactor");
		addUniform("waterColor");
		addUniform("capillarStrength");
		addUniform("capillarDownsampling");
		addUniform("dudvDownsampling");
		
		addUniform("diffuseEnable");
		addUniform("emission");
		addUniform("specularFactor");
		addUniform("specularAmplifier");

		addUniform("isCameraUnderWater");
		
		addUniform("normalmap");
		
		addUniform("Dy");
		addUniform("Dx");
		addUniform("Dz");
		addUniform("motion");
		addUniform("wind");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
		
		addUniformBlock("DirectionalLight");
	}
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		
		setUniform("projectionViewMatrix", BaseContext.getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
				
		setUniform("eyePosition", BaseContext.getCamera().getPosition());
		setUniformi("windowWidth", BaseContext.getWindow().getWidth());
		setUniformi("windowHeight", BaseContext.getWindow().getHeight());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", BaseContext.getCamera().getFrustumPlanes()[i]);
		}
		
		Water ocean = (Water) object;
		WaterConfiguration configuration = ocean.getWaterConfiguration();
		
		setUniformf("displacementScale", configuration.getDisplacementScale());
		setUniformf("choppiness", configuration.getChoppiness());
		setUniformi("texDetail", configuration.getUvScale());
		setUniformi("tessFactor", configuration.getTessellationFactor());
		setUniformf("tessSlope", configuration.getTessellationSlope());
		setUniformf("tessShift", configuration.getTessellationShift());
		setUniformi("largeDetailRange", configuration.getHighDetailRange());
		setUniformf("distortion", configuration.getDistortion());
		setUniformf("distortion", configuration.isDiffuse() ? 1 : 0);
		setUniformf("emission", configuration.getEmission());
		setUniformf("specularFactor", configuration.getSpecularFactor());
		setUniformf("specularAmplifier", configuration.getSpecularAmplifier());
		setUniformf("motion", ocean.getMotion());
		setUniformi("isCameraUnderWater", BaseContext.getConfig().isRenderUnderwater() ? 1 : 0);
		setUniformf("fresnelFactor", configuration.getFresnelFactor());
		setUniformf("reflectionBlendFactor", configuration.getReflectionBlendFactor());
		setUniform("waterColor", configuration.getBaseColor());
		setUniformf("capillarStrength", configuration.getCapillarStrength());		
		setUniformf("capillarDownsampling", configuration.getCapillarDownsampling());		
		setUniformf("dudvDownsampling", configuration.getDudvDownsampling());		
		setUniform("wind", configuration.getWindDirection());
		
		glActiveTexture(GL_TEXTURE0);
		ocean.getDudv().bind();
		setUniformi("dudvMap", 0);
		glActiveTexture(GL_TEXTURE1);
		ocean.getReflectionTexture().bind();
		setUniformi("waterReflection", 1);
		setUniformf("kReflection", configuration.getKReflection());
		glActiveTexture(GL_TEXTURE2);
		ocean.getRefractionTexture().bind();
		setUniformi("waterRefraction", 2);
		setUniformf("kRefraction", configuration.getKRefraction());
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
