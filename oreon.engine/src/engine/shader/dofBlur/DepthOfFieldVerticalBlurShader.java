package engine.shader.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.Window;
import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

public class DepthOfFieldVerticalBlurShader extends Shader{

private static DepthOfFieldVerticalBlurShader instance = null;
	
	public static DepthOfFieldVerticalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new DepthOfFieldVerticalBlurShader();
	    }
	      return instance;
	}
	
	protected DepthOfFieldVerticalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/depthOfFieldBlur/verticalGaussianDoF_CS.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
		
		for (int i=0; i<7; i++){
			addUniform("gaussianKernel7[" + i + "]");
		}
	}
	
	public void updateUniforms(Texture2D depthmap, float[] gaussianKernel_7)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", Window.getInstance().getWidth());
		setUniformf("windowHeight", Window.getInstance().getHeight());
		
		for (int i=0; i<7; i++){
			setUniformf("gaussianKernel7[" + i + "]", gaussianKernel_7[i]);
		}
	}
}
