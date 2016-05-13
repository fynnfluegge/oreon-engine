package engine.shaders.motionblur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.ResourceLoader;
import engine.core.Texture;
import engine.core.OpenGLWindow;
import engine.math.Matrix4f;
import engine.shaders.Shader;

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
		
		addComputeShader(ResourceLoader.loadShader("gpgpu/Motion Blur/PixelVelocity.glsl"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
		addUniform("projectionMatrix");
		addUniform("inverseViewProjectionMatrix");
		addUniform("previousViewProjectionMatrix");
	}
	
	public void sendUniforms(Matrix4f projectionMatrix, Matrix4f inverseViewProjectionMatrix, Matrix4f previousViewProjectionMatrix, Texture depthmap)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", OpenGLWindow.getWidth());
		setUniformf("windowHeight", OpenGLWindow.getHeight());
		setUniform("projectionMatrix", projectionMatrix);
		setUniform("inverseViewProjectionMatrix", inverseViewProjectionMatrix);
		setUniform("previousViewProjectionMatrix", previousViewProjectionMatrix);
	}
}
