package org.oreon.modules.gl.postprocessfilter.ssao;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class NoiseTextureShader extends GLShader{
	
	private static NoiseTextureShader instance = null;
	
	public static NoiseTextureShader getInstance()
	{
		if (instance == null){
			
			instance = new NoiseTextureShader();
		}
		return instance;
	}
	
	protected NoiseTextureShader(){
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/post_processing/ssao/NoiseTexture_CS.glsl"));
		compileShader();
		
		for (int i=0; i<16; i++){
			addUniform("randomx[" + i + "]");
		}
		
		for (int i=0; i<16; i++){
			addUniform("randomy[" + i + "]");
		}
	}
	
	public void updateUniforms(int[] randomx, int[] randomy){
		
		for (int i=0; i<16; i++){
			setUniformi("randomx[" + i + "]", randomx[i]);
		}
		
		for (int i=0; i<16; i++){
			setUniformi("randomy[" + i + "]", randomy[i]);
		}
	}

}
