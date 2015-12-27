package engine.shaderprograms;

import engine.core.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("compute/MotionBlur.glsl"));
		
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
