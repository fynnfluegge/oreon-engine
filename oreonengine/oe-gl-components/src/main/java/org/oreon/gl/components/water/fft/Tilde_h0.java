package org.oreon.gl.components.water.fft;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilter;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.gpgpu.fft.FourierSpectrum;
import org.oreon.gl.components.water.shader.Tilde_h0Shader;

import lombok.Getter;

public class Tilde_h0 extends FourierSpectrum{
	
	@Getter
	private GLTexture h0k;
	@Getter
	private GLTexture h0kminus;
	
	private Vec2f wind = new Vec2f(1,1).normalize();
	private float windspeed = 25;
	private float A = 2f;
	private GLTexture noise0;
	private GLTexture noise1;
	private GLTexture noise2;
	private GLTexture noise3;
	
	public Tilde_h0(int N, int L)
	{
		super(N,L);
		
		setShader(Tilde_h0Shader.getInstance());
		
		h0k = new Texture2DStorageRGBA32F(N,N,1);
		h0kminus = new Texture2DStorageRGBA32F(N,N,1);
		noise0 = new Texture2DNoFilter("textures/noise/Noise256_0.jpg");
		noise1 = new Texture2DNoFilter("textures/noise/Noise256_1.jpg");
		noise2 = new Texture2DNoFilter("textures/noise/Noise256_2.jpg");
		noise3 = new Texture2DNoFilter("textures/noise/Noise256_3.jpg");
	}
	
	@Override
	public void render() {
		
		getShader().bind();
		getShader().updateUniforms(getN(), L, A, wind, windspeed);
		
		glActiveTexture(GL_TEXTURE0);
		noise0.bind();
		
		glActiveTexture(GL_TEXTURE1);
		noise1.bind();
		
		glActiveTexture(GL_TEXTURE2);
		noise2.bind();
		
		glActiveTexture(GL_TEXTURE3);
		noise3.bind();
		
		getShader().updateUniforms(0, 1, 2, 3);
		
		glBindImageTexture(0, h0k.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		
		glBindImageTexture(1, h0kminus.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		
		glDispatchCompute(getN()/16,getN()/16,1);		
	}

}
