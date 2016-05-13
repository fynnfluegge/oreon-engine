package engine.shaders.motionblur;

import engine.core.ResourceLoader;
import engine.shaders.Shader;

public class MotionBlurShader extends Shader{
	
	private static MotionBlurShader instance = null;
	
	public static MotionBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new MotionBlurShader();
	    }
	      return instance;
	}
	
	protected MotionBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("gpgpu/Motion Blur/MotionBlur.glsl"));
		
		compileShader();
		
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void sendUniforms(int width, int height)
	{		
		setUniformf("windowWidth", width);
		setUniformf("windowHeight", height);
	}

}
