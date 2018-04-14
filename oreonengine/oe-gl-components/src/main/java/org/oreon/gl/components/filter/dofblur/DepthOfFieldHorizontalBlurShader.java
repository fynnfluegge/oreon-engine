package org.oreon.gl.components.filter.dofblur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.util.ResourceLoader;

public class DepthOfFieldHorizontalBlurShader extends GLShaderProgram{

	private static DepthOfFieldHorizontalBlurShader instance = null;
	
	public static DepthOfFieldHorizontalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new DepthOfFieldHorizontalBlurShader();
	    }
	      return instance;
	}
	
	protected DepthOfFieldHorizontalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/DepthOfFieldBlur/horizontalGaussianDoF_CS.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2DMultisample depthmap){
		
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", EngineContext.getWindow().getWidth());
		setUniformf("windowHeight", EngineContext.getWindow().getHeight());
	}
}
