package engine.shader.motionblur;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/motionBlur/MotionBlur.glsl"));
		
		compileShader();
		
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(int width, int height)
	{		
		setUniformf("windowWidth", width);
		setUniformf("windowHeight", height);
	}

}
