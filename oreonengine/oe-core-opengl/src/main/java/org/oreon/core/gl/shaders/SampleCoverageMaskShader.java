package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class SampleCoverageMaskShader extends GLShader{
	
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/anti-aliasing/sampleMaskCoverage_CS.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("multisamples");
	}
	
	public void updateUniforms(Texture2DMultisample depthmap){
		
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformi("multisamples", Constants.MULTISAMPLES);
	}

}
