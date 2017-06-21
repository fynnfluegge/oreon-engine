package org.oreon.engine.modules.atmosphere;

import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class SunShader extends Shader{
	
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
		
	public void updateUniforms(GameObject object)
	{
		setUniform("m_MVP", object.getTransform().getModelViewProjectionMatrix());
		
		glActiveTexture(GL_TEXTURE0);
		((Material) object.getComponents().get("Material1")).getDiffusemap().bind();
		setUniformi("sunTexture", 0);
		
		glActiveTexture(GL_TEXTURE1);
		((Material) object.getComponents().get("Material2")).getDiffusemap().bind();
		setUniformi("sunTexture_small", 1);
	}
}
