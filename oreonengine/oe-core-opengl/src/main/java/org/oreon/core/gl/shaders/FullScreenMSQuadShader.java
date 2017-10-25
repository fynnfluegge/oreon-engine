package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.Constants;
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
		addUniform("width");
		addUniform("height");
		addUniform("multisamples");
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
		
		setUniformi("width", CoreSystem.getInstance().getWindow().getWidth());
		setUniformi("height", CoreSystem.getInstance().getWindow().getHeight());
		setUniformi("multisamples", Constants.MULTISAMPLES);		
	}
}
