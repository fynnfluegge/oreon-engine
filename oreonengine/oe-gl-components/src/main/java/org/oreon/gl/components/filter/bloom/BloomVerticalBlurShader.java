package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
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
		
		addUniform("horizontalBloomBlurSampler0");
		addUniform("horizontalBloomBlurSampler1");
		addUniform("horizontalBloomBlurSampler2");
		addUniform("horizontalBloomBlurSampler3");
		addUniform("width");
		addUniform("height");
		
		for (int i=0; i<4; i++){
			addUniform("downsamplingFactors[" + i + "]");
		}
	}
	
	public void updateUniforms(GLTexture horizontalBloomBlurSampler0, GLTexture horizontalBloomBlurSampler1,
			GLTexture horizontalBloomBlurSampler2, GLTexture horizontalBloomBlurSampler3,
			int[] downsamplingFactors, int width, int height)
	{
		glActiveTexture(GL_TEXTURE0);
		horizontalBloomBlurSampler0.bind();
		setUniformi("horizontalBloomBlurSampler0", 0);
		glActiveTexture(GL_TEXTURE1);
		horizontalBloomBlurSampler1.bind();
		setUniformi("horizontalBloomBlurSampler1", 1);
		glActiveTexture(GL_TEXTURE2);
		horizontalBloomBlurSampler2.bind();
		setUniformi("horizontalBloomBlurSampler2", 2);
		glActiveTexture(GL_TEXTURE3);
		horizontalBloomBlurSampler3.bind();
		setUniformi("horizontalBloomBlurSampler3", 3);
		
		setUniformf("width", width);
		setUniformf("height", height);
		
		for (int i=0; i<4; i++){
			setUniformf("downsamplingFactors[" + i + "]", downsamplingFactors[i]);
		}
	}
}
