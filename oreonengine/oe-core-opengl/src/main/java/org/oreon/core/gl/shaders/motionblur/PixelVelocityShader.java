package org.oreon.core.gl.shaders.motionblur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/MotionBlur/PixelVelocity.glsl"));
		
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
		setUniformf("windowWidth", CoreSystem.getInstance().getWindow().getWidth());
		setUniformf("windowHeight", CoreSystem.getInstance().getWindow().getHeight());
		setUniform("projectionMatrix", projectionMatrix);
		setUniform("inverseViewProjectionMatrix", inverseViewProjectionMatrix);
		setUniform("previousViewProjectionMatrix", previousViewProjectionMatrix);
	}
}
