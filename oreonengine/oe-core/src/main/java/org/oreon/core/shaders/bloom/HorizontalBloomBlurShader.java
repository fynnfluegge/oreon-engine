package org.oreon.core.shaders.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.shaders.Shader;
import org.oreon.core.utils.ResourceLoader;

public class HorizontalBloomBlurShader extends Shader{

	private static HorizontalBloomBlurShader instance = null;
	
	public static HorizontalBloomBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new HorizontalBloomBlurShader();
	    }
	      return instance;
	}
	
	protected HorizontalBloomBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Bloom/horizontalGaussianBloom_CS.glsl"));
		
		compileShader();
		
		addUniform("bloomSampler");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2D bloomTexture, int width, int height)
	{
		glActiveTexture(GL_TEXTURE0);
		bloomTexture.bind();
		setUniformi("bloomSampler", 0);
		setUniformf("windowWidth", width);
		setUniformf("windowHeight", height);
	}
}
