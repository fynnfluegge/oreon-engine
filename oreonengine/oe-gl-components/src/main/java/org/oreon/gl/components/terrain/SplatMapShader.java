package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
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
	
//		addUniform("normalmap");
		addUniform("heightmap");
		addUniform("N");
		addUniform("yScale");
	}
	
	public void updateUniforms(GLTexture normalmap, GLTexture heightmap, int N, float yScale)
	{
//		glActiveTexture(GL_TEXTURE0);
//		normalmap.bind();
//		setUniformi("normalmap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		heightmap.bind();
		setUniformi("heightmap", 1);
		
		setUniformi("N", N);
		setUniformf("yScale", yScale);
	}
}
