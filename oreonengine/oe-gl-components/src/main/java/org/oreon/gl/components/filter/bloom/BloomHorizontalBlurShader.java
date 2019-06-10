package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class BloomHorizontalBlurShader extends GLShaderProgram{

	private static BloomHorizontalBlurShader instance = null;
	
	public static BloomHorizontalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomHorizontalBlurShader();
	    }
	      return instance;
	}
	
	protected BloomHorizontalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloom_horizontalGaussianBlur.comp"));
		
		compileShader();
		
		addUniform("sceneBrightnessSampler");
		addUniform("width0");
		addUniform("height0");
		addUniform("width1");
		addUniform("height1");
		addUniform("width2");
		addUniform("height2");
		addUniform("width3");
		addUniform("height3");
	}
	
	public void updateUniforms(GLTexture sceneBrightnessSampler, 
			int width0, int height0, int width1, int height1, int width2, int height2, int width3, int height3)
	{
		glActiveTexture(GL_TEXTURE0);
		sceneBrightnessSampler.bind();
		setUniformi("sceneBrightnessSampler", 0);
		setUniformf("width0", width0);
		setUniformf("height0", height0);
		setUniformf("width1", width1);
		setUniformf("height1", height1);
		setUniformf("width2", width2);
		setUniformf("height2", height2);
		setUniformf("width3", width3);
		setUniformf("height3", height3);
	}
}
