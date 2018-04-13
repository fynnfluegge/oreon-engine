package org.oreon.modules.gl.water.shader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.util.ResourceLoader;

public class UnderWaterShader extends GLShaderProgram{

	private static UnderWaterShader instance = null;
	
	public static UnderWaterShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new UnderWaterShader();
	    }
	      return instance;
	}
	
	protected UnderWaterShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/post_processing/underwater/underwater_CS.glsl"));
		
		compileShader();
		
		addUniform("sceneDepthMap");
	}
	
	public void updateUniforms(Texture2DMultisample sceneDepthMap) {
		glActiveTexture(GL_TEXTURE0);
		sceneDepthMap.bind();
		setUniformi("sceneDepthMap", 0);
	}
}
