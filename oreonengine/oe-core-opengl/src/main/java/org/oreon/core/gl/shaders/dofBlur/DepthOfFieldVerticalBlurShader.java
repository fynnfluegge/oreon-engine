package org.oreon.core.gl.shaders.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.ResourceLoader;

public class DepthOfFieldVerticalBlurShader extends GLShader{

private static DepthOfFieldVerticalBlurShader instance = null;
	
	public static DepthOfFieldVerticalBlurShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new DepthOfFieldVerticalBlurShader();
	    }
	      return instance;
	}
	
	protected DepthOfFieldVerticalBlurShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/DepthOfFieldBlur/verticalGaussianDoF_CS.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2D depthmap)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", CoreSystem.getInstance().getWindow().getWidth());
		setUniformf("windowHeight", CoreSystem.getInstance().getWindow().getHeight());
	}
}
