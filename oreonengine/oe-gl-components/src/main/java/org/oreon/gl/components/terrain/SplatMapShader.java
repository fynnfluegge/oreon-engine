package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoader;

public class SplatMapShader extends GLShaderProgram{

	private static SplatMapShader instance = null;

	public static SplatMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SplatMapShader();
	    }
	      return instance;
	}
	
	protected SplatMapShader() {
	
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/SplatMap_CS.glsl"));
		compileShader();
	
		addUniform("normalmap");
		addUniform("N");
	}
	
	public void updateUniforms(GLTexture normalmap, int N)
	{
		glActiveTexture(GL_TEXTURE0);
		normalmap.bind();
		setUniformi("normalmap", 0);
		setUniformi("N", N);
	}
}
