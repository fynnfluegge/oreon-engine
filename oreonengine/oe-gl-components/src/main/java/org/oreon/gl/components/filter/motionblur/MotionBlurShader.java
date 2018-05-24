package org.oreon.gl.components.filter.motionblur;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class MotionBlurShader extends GLShaderProgram{
	
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/motionBlur/motionBlur.comp"));
		
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
