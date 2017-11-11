package org.oreon.modules.gl.postprocessfilter.ssao;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class SSAOBlurShader extends GLShader{

	private static SSAOBlurShader instance = null;
	
	public static SSAOBlurShader getInstance()
	{
		if (instance == null){
			
			instance = new SSAOBlurShader();
		}
		return instance;
	}
	
	protected SSAOBlurShader(){
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/post_processing/ssao/SSAOBlur_CS.glsl"));
		compileShader();
	}
}
