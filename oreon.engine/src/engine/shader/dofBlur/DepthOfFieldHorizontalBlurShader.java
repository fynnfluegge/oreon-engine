package engine.shader.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.Window;
import engine.shader.Shader;
import engine.texturing.Texture;
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
		for (int i=0; i<9; i++){
			addUniform("gaussianKernel9[" + i + "]");
		}
	}
	
	public void updateUniforms(Texture depthmap, float[] gaussianKernel)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", Window.getInstance().getWidth());
		setUniformf("windowHeight", Window.getInstance().getHeight());
		for (int i=0; i<9; i++){
			setUniformf("gaussianKernel9[" + i + "]", gaussianKernel[i]);
		}
	}
}
