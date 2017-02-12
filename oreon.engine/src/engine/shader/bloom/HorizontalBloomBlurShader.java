package engine.shader.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/bloom/horizontalGaussianBloom_CS.glsl"));
		
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
