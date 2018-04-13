package org.oreon.core.gl.shaders;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class SampleCoverageMaskShader extends GLShaderProgram{
	
	private static SampleCoverageMaskShader instance = null;

	public static SampleCoverageMaskShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SampleCoverageMaskShader();
	    }
	      return instance;
	}
	
	protected SampleCoverageMaskShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/anti-aliasing/sampleCoverageMask_CS.glsl"));
		
		compileShader();
		
		addUniform("multisamples");
	}
	
	public void updateUniforms(){
		
		setUniformi("multisamples", EngineContext.getConfig().getMultisamples());
	}

}
