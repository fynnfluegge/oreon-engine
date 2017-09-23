package org.oreon.core.gl.shaders.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;

public class DepthOfFieldHorizontalBlurShader extends Shader{

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
	
	public void updateUniforms(Texture2D depthmap){
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", CoreSystem.getInstance().getWindow().getWidth());
		setUniformf("windowHeight", CoreSystem.getInstance().getWindow().getHeight());
	}
}
