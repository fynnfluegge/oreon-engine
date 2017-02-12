package engine.shader.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

public class BloomBlurAdditiveBlendShader extends Shader{

private static BloomBlurAdditiveBlendShader instance = null;
	
	public static BloomBlurAdditiveBlendShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomBlurAdditiveBlendShader();
	    }
	      return instance;
	}
	
	protected BloomBlurAdditiveBlendShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/BloomBlurAdditiveBlend_CS.glsl"));
		
		compileShader();
		
		addUniform("bloomBlurSampler_div2");
		addUniform("bloomBlurSampler_div4");
		addUniform("bloomBlurSampler_div8");
		addUniform("bloomBlurSampler_div16");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2D bloomBlurTexture_div2,Texture2D bloomBlurTexture_div4,Texture2D bloomBlurTexture_div8,Texture2D bloomBlurTexture_div16,int width,int height)
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
