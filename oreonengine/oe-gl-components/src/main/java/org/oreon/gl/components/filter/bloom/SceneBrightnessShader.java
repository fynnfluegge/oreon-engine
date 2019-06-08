package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class SceneBrightnessShader extends GLShaderProgram{
	
	private static SceneBrightnessShader instance = null;
	
	public static SceneBrightnessShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SceneBrightnessShader();
	    }
	      return instance;
	}
	
	protected SceneBrightnessShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/bloom/brightness.comp"));
		compileShader();
		
		addUniform("sceneSampler");
	}
	
	public void updateUniforms(GLTexture sceneSampler){
		
		glActiveTexture(GL_TEXTURE1);
		sceneSampler.bind();
		setUniformi("sceneSampler", 1);
	}
}
