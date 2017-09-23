package org.oreon.core.gl.shaders.lensFlare;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.shaders.Shader;
import org.oreon.core.utils.ResourceLoader;

public class LensFlareShader extends Shader{

	private static LensFlareShader instance = null;
	
	public static LensFlareShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new LensFlareShader();
	    }
	    return instance;
	}
	
	protected LensFlareShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/post_processing/lens_flare/LensFlare_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/post_processing/lens_flare/LensFlare_FS.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("texture");
		addUniform("transparency");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(Texture2D texture, float transparency)
	{
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		setUniformi("texture", 0);
		setUniformf("transparency", transparency);
	}
}
