package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class BloomVerticalBlurShader extends GLShaderProgram{

	private static BloomVerticalBlurShader instance = null;
	
	public static BloomVerticalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomVerticalBlurShader();
	    }
	      return instance;
	}
	
	protected BloomVerticalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloom_verticalGaussianBlur.comp"));
		
		compileShader();
		
		addUniform("horizontalBloomBlurSampler");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(GLTexture horizontalBloomBlurSampler, int width, int height)
	{
		glActiveTexture(GL_TEXTURE0);
		horizontalBloomBlurSampler.bind();
		setUniformi("horizontalBloomBlurSampler", 0);
		setUniformf("windowWidth", width);
		setUniformf("windowHeight", height);
	}
}
