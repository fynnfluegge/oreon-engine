package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class DeferredLightingShader extends GLShaderProgram{

	private static DeferredLightingShader instance = null;
	
	public static DeferredLightingShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new DeferredLightingShader();
		}
		return instance;
	}
		
	protected DeferredLightingShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/deferredLighting_CS.glsl"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("LightViewProjections");
		addUniform("numSamples");
		addUniform("pssm");
		addUniform("sightRangeFactor");
		addUniform("flag");
	}
	
	public void updateUniforms(GLTexture pssm, boolean flag){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformf("sightRangeFactor", EngineContext.getConfig().getSightRange());
		
		glActiveTexture(GL_TEXTURE1);
		pssm.bind();
		setUniformi("pssm", 1);
		
		setUniformi("flag", flag ? 1 : 0);
		
		setUniformi("numSamples", EngineContext.getConfig().getMultisamples());
	}
}
