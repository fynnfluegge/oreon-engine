package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class AtmosphereShader extends GLShaderProgram{

	private static AtmosphereShader instance = null;
	
	public static AtmosphereShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new AtmosphereShader();
		}
		return instance;
	}
		
	protected AtmosphereShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/atmosphere/atmosphere.vert"));
		addFragmentShader(ResourceLoader.loadShader("shaders/atmosphere/atmosphere.frag"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("modelMatrix");
		addUniform("m_ViewProjection");
		addUniform("v_SunWorld");
		addUniform("r_Sun");
		addUniform("width");
		addUniform("height");
		addUniform("isReflection");
		addUniform("bloom");
	}
		
	public void updateUniforms(Renderable object)
	{
		setUniform("modelViewProjectionMatrix", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("modelMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("m_ViewProjection", BaseContext.getCamera().getOriginViewProjectionMatrix());
		setUniform("v_SunWorld", BaseContext.getConfig().getSunPosition().mul(-Constants.ZFAR));
		setUniformf("r_Sun", BaseContext.getConfig().getSunRadius());
		setUniformi("width", BaseContext.getConfig().getFrameWidth());
		setUniformi("height", BaseContext.getConfig().getFrameHeight());
		setUniformi("isReflection", BaseContext.getConfig().isRenderReflection() ? 1 : 0);
		setUniformf("bloom", BaseContext.getConfig().getAtmosphereBloomFactor());
	}
}
