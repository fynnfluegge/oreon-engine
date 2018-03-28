package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.util.ResourceLoader;

public class FXAAShader extends GLShader{

	private static FXAAShader instance = null;
	
	public static FXAAShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new FXAAShader();
		}
		return instance;
	}
		
	protected FXAAShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/anti-aliasing/fxaa_CS.glsl"));
		
		compileShader();
		
		addUniform("sceneTexture");
		addUniform("width");
		addUniform("height");
	} 
	
	public void updateUniforms(Texture2D sceneTexture){
		
		glActiveTexture(GL_TEXTURE0);
		sceneTexture.bind();
		setUniformi("sceneTexture", 0);
		setUniformi("width", EngineContext.getWindow().getWidth());
		setUniformi("height", EngineContext.getWindow().getHeight());
	}
}
