package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class BloomAdditiveBlendShader extends GLShaderProgram{

	private static BloomAdditiveBlendShader instance = null;
	
	public static BloomAdditiveBlendShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomAdditiveBlendShader();
	    }
	      return instance;
	}
	
	protected BloomAdditiveBlendShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/bloom_gaussianBlending.comp"));
		
		compileShader();
		
		addUniform("bloomBlurSampler_div2");
		addUniform("bloomBlurSampler_div4");
		addUniform("bloomBlurSampler_div8");
		addUniform("bloomBlurSampler_div16");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(GLTexture bloomBlurTexture_div2,
			GLTexture bloomBlurTexture_div4, GLTexture bloomBlurTexture_div8, 
			GLTexture bloomBlurTexture_div16,int width,int height)
	{
		glActiveTexture(GL_TEXTURE1);
		bloomBlurTexture_div2.bind();
		setUniformi("bloomBlurSampler_div2", 1);
		glActiveTexture(GL_TEXTURE2);
		bloomBlurTexture_div4.bind();
		setUniformi("bloomBlurSampler_div4", 2);
		glActiveTexture(GL_TEXTURE3);
		bloomBlurTexture_div4.bind();
		setUniformi("bloomBlurSampler_div8", 3);
		glActiveTexture(GL_TEXTURE0);
		bloomBlurTexture_div16.bind();
		setUniformi("bloomBlurSampler_div16", 0);
		setUniformf("windowWidth", width);
		setUniformf("windowHeight", height);
	}
}
