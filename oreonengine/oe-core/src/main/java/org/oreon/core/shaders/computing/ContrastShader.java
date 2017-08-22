package org.oreon.core.shaders.computing;

import org.oreon.core.shaders.Shader;
import org.oreon.core.utils.ResourceLoader;

public class ContrastShader extends Shader{

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
