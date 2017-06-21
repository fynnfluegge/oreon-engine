package org.oreon.engine.engine.shaders.underwater;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.engine.engine.core.Window;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.textures.Texture2D;
import org.oreon.engine.engine.utils.ResourceLoader;

public class UnderWaterShader extends Shader{

	private static UnderWaterShader instance = null;
	
	public static UnderWaterShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new UnderWaterShader();
	    }
	      return instance;
	}
	
	protected UnderWaterShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/post_processing/underwater/underwater_CS.glsl"));
		
		compileShader();
		
		addUniform("sceneDepthMap");
		addUniform("windowWidth");
		addUniform("windowHeight");
	}
	
	public void updateUniforms(Texture2D sceneDepthMap) {
		glActiveTexture(GL_TEXTURE0);
		sceneDepthMap.bind();
		setUniformi("sceneDepthMap", 0);
		
		setUniformf("windowWidth", Window.getInstance().getWidth());
		setUniformf("windowHeight", Window.getInstance().getHeight());
	}
}
