package org.oreon.core.gl.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.texture.Texture2DArray;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class DeferredLightingShader extends GLShader{

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
		
		addComputeShader(ResourceLoader.loadShader("shaders/deferred/deferredLighting_CS.glsl"));
		compileShader();
		
		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
		addUniformBlock("LightViewProjections");
		addUniform("numSamples");
		addUniform("pssm");
		addUniform("sightRangeFactor");
		addUniform("flag");
	}
	
	public void updateUniforms(Texture2DArray pssm, boolean flag){
		
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);	
		bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
		setUniformf("sightRangeFactor", CoreSystem.getInstance().getRenderEngine().getSightRangeFactor());
		
		glActiveTexture(GL_TEXTURE1);
		pssm.bind();
		setUniformi("pssm", 1);
		
		setUniformi("flag", flag ? 1 : 0);
		
		setUniformi("numSamples", Constants.MULTISAMPLES);
	}
}
