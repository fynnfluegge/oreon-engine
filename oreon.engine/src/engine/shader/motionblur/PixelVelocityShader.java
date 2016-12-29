package engine.shader.motionblur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.core.Window;
import engine.math.Matrix4f;
import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.ResourceLoader;

public class PixelVelocityShader extends Shader{
	
	private static PixelVelocityShader instance = null;
	
	public static PixelVelocityShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PixelVelocityShader();
	    }
	      return instance;
	}
	
	protected PixelVelocityShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/motionBlur/PixelVelocity.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
		addUniform("projectionMatrix");
		addUniform("inverseViewProjectionMatrix");
		addUniform("previousViewProjectionMatrix");
	}
	
	public void updateUniforms(Matrix4f projectionMatrix, Matrix4f inverseViewProjectionMatrix, Matrix4f previousViewProjectionMatrix, Texture2D depthmap)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", Window.getInstance().getWidth());
		setUniformf("windowHeight", Window.getInstance().getHeight());
		setUniform("projectionMatrix", projectionMatrix);
		setUniform("inverseViewProjectionMatrix", inverseViewProjectionMatrix);
		setUniform("previousViewProjectionMatrix", previousViewProjectionMatrix);
	}
}
