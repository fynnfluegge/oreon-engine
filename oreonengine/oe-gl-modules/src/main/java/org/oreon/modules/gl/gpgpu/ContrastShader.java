package org.oreon.modules.gl.gpgpu;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class ContrastShader extends GLShader{

	private static ContrastShader instance = null;

	public static ContrastShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new ContrastShader();
	    }
	      return instance;
	}
	
	protected ContrastShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Contrast/Contrast_CS.glsl"));
		
		compileShader();
		
		addUniform("contrastFactor");
		addUniform("brightnessFactor");
	}
	
	public void updateUniforms(float contrastFactor, float brightnessFactor){
		setUniformf("contrastFactor", contrastFactor);
		setUniformf("brightnessFactor", brightnessFactor);
	}
}
