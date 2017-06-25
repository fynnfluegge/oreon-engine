package engine.shaders.lensFlare;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.math.Matrix4f;
import engine.shaders.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

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
//		addUniform("transparency");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(Texture2D texture, float transparency)
	{
		glActiveTexture(GL_TEXTURE1);
		texture.bind();
		setUniformi("texture", 1);
//		setUniformf("transparency", transparency);
	}
}
