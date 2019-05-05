package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.ResourceLoader;

public class AtmosphericScatteringShader extends GLShaderProgram{

	private static AtmosphericScatteringShader instance = null;
	
	public static AtmosphericScatteringShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new AtmosphericScatteringShader();
		}
		return instance;
	}
		
	protected AtmosphericScatteringShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/atmosphere/atmospheric_scattering.vert"));
		addFragmentShader(ResourceLoader.loadShader("shaders/atmosphere/atmospheric_scattering.frag"));
		compileShader();
			
		addUniform("m_MVP");
		addUniform("m_Projection");
		addUniform("m_View");
		addUniform("v_Sun");
		addUniform("r_Sun");
		addUniform("width");
		addUniform("height");
		addUniform("isReflection");
	}
	
	public void updateUniforms(Renderable object)
	{
		setUniform("m_MVP", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("m_Projection", BaseContext.getCamera().getProjectionMatrix());
		setUniform("m_View", BaseContext.getCamera().getViewMatrix());
		setUniform("v_Sun", BaseContext.getConfig().getSunPosition().mul(-1));
		setUniformf("r_Sun", BaseContext.getConfig().getSunRadius());
		setUniformi("width", BaseContext.getConfig().getX_ScreenResolution());
		setUniformi("height", BaseContext.getConfig().getY_ScreenResolution());
		setUniformi("isReflection", BaseContext.getConfig().isRenderReflection() ? 1 : 0);
	}

}
