package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.ResourceLoader;

public class FullScreenMSQuadShader extends GLShader{

	private static FullScreenMSQuadShader instance = null;
	
	public static FullScreenMSQuadShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FullScreenMSQuadShader();
	    }
	    return instance;
	}
	
	protected FullScreenMSQuadShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/quad/quad_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/quad/quadMS_FS.glsl"));
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
