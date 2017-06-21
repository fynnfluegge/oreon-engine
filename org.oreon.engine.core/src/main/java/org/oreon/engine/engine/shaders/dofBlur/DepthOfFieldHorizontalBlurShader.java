package org.oreon.engine.engine.shaders.dofBlur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.engine.engine.core.Window;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.textures.Texture2D;
import org.oreon.engine.engine.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/depthOfFieldBlur/horizontalGaussianDoF_CS.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2D depthmap){
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", Window.getInstance().getWidth());
		setUniformf("windowHeight", Window.getInstance().getHeight());
	}
}
