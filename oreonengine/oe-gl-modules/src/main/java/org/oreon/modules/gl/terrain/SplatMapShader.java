package org.oreon.modules.gl.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.util.ResourceLoader;

public class SplatMapShader extends GLShader{

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
	
	public void updateUniforms(Texture2D normalmap, int N)
	{
		glActiveTexture(GL_TEXTURE0);
		normalmap.bind();
		setUniformi("normalmap", 0);
		setUniformi("N", N);
	}
}
