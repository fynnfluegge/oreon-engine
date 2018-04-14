package org.oreon.gl.components.atmosphere;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.ResourceLoader;

public class SunShader extends GLShaderProgram{
	
private static SunShader instance = null;
	
	public static SunShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new SunShader();
		}
		return instance;
	}
		
	protected SunShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/atmosphere/sun_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/atmosphere/sun_FS.glsl"));
		compileShader();
			
		addUniform("m_MVP");
		addUniform("sunTexture");
		addUniform("sunTexture_small");
	}
		
	public void updateUniforms(Renderable object)
	{
		setUniform("m_MVP", object.getWorldTransform().getModelViewProjectionMatrix());
		
		glActiveTexture(GL_TEXTURE0);
		((Material) object.getComponents().get(NodeComponentType.MATERIAL0)).getDiffusemap().bind();
		setUniformi("sunTexture", 0);
		
		glActiveTexture(GL_TEXTURE1);
		((Material) object.getComponents().get(NodeComponentType.MATERIAL1)).getDiffusemap().bind();
		setUniformi("sunTexture_small", 1);
	}
}
