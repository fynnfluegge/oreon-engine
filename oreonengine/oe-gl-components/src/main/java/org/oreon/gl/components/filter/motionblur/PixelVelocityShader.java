package org.oreon.gl.components.filter.motionblur;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.ResourceLoader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class PixelVelocityShader extends GLShaderProgram{
	
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
		
		addComputeShader(ResourceLoader.loadShader("shaders/filter/motionBlur/pixelVelocity.comp"));
		
		compileShader();
		
		addUniform("depthmap");
		addUniform("windowWidth");
		addUniform("windowHeight");
		addUniform("projectionMatrix");
		addUniform("inverseViewProjectionMatrix");
		addUniform("previousViewProjectionMatrix");
	}
	
	public void updateUniforms(Matrix4f projectionMatrix,
							   Matrix4f inverseViewProjectionMatrix, 
							   Matrix4f previousViewProjectionMatrix, 
							   GLTexture depthmap)
	{
		glActiveTexture(GL_TEXTURE0);
		depthmap.bind();
		setUniformi("depthmap", 0);
		setUniformf("windowWidth", BaseContext.getWindow().getWidth());
		setUniformf("windowHeight", BaseContext.getWindow().getHeight());
		setUniform("projectionMatrix", projectionMatrix);
		setUniform("inverseViewProjectionMatrix", inverseViewProjectionMatrix);
		setUniform("previousViewProjectionMatrix", previousViewProjectionMatrix);
	}
}
