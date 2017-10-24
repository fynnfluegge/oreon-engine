package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.ResourceLoader;

public class FullScreenQuadShader extends GLShader{

	private static FullScreenQuadShader instance = null;
	
	public static FullScreenQuadShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FullScreenQuadShader();
	    }
	    return instance;
	}
	
	protected FullScreenQuadShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/quad/quad_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/quad/quad_FS.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("texture");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(Texture texture)
	{
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		setUniformi("texture", 0);
	}
}
