package engine.shader.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.Window;
import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

public class DepthOfFieldHorizontalBlurShader extends Shader{

private static DepthOfFieldHorizontalBlurShader instance = null;
	
	public static DepthOfFieldHorizontalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new DepthOfFieldHorizontalBlurShader();
	    }
	      return instance;
	}
	
	protected DepthOfFieldHorizontalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/dofBlur/horizontalGaussian.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");

		for (int i=0; i<7; i++){
			addUniform("gaussianKernel7[" + i + "]");
		}
	}
	
	public void updateUniforms(Texture2D depthmap, float[] gaussianKernel_7){
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
