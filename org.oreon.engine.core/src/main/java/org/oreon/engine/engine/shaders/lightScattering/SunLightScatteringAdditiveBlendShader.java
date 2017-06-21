package org.oreon.engine.engine.shaders.lightScattering;

import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

public class SunLightScatteringAdditiveBlendShader extends Shader{

	private static SunLightScatteringAdditiveBlendShader instance = null;
	
	public static SunLightScatteringAdditiveBlendShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SunLightScatteringAdditiveBlendShader();
	    }
	      return instance;
	}
	
	protected SunLightScatteringAdditiveBlendShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/lightScattering/additiveBlend_CS.glsl"));
		
		compileShader();
	}
}
