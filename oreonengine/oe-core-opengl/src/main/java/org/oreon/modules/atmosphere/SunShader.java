package org.oreon.modules.atmosphere;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.utils.ResourceLoader;

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
		setUniform("m_MVP", object.getWorldTransform().getModelViewProjectionMatrix());
		
		glActiveTexture(GL_TEXTURE0);
		((Material) object.getComponents().get("Material1")).getDiffusemap().bind();
		setUniformi("sunTexture", 0);
		
		glActiveTexture(GL_TEXTURE1);
		((Material) object.getComponents().get("Material2")).getDiffusemap().bind();
		setUniformi("sunTexture_small", 1);
	}
}
