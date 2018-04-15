package org.oreon.gl.components.terrain.fractals;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.gpgpu.fft.FFTButterflyShader;
import org.oreon.gl.components.gpgpu.fft.FFTInversionShader;
import org.oreon.gl.components.gpgpu.fft.FastFourierTransform;

import lombok.Getter;

public class FractalFFT extends FastFourierTransform{

	@Getter
	private GLTexture heightmap;
	
	public FractalFFT(int N, int L, float A, float v, Vec2f w, float l) {
			
		super(N);
		
		setFourierComponents(new FractalFourierComponents(N, L, A, v, w, l));
		setButterflyShader(FFTButterflyShader.getInstance());
		setInversionShader(FFTInversionShader.getInstance());
		heightmap = new Texture2DStorageRGBA32F(N,N,1);
		heightmap.bind();
		heightmap.trilinearFilter();
		heightmap.unbind();
		
		setPingpongTexture(new Texture2DStorageRGBA32F(N,N,1));
	}

	public void render()
	{
		getFourierComponents().update(t);
		
		pingpong = 0;
		
		getButterflyShader().bind();
		
		glBindImageTexture(0, getTwiddles().getTexture().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, ((FractalFourierComponents) getFourierComponents()).getFourierComponents().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, getPingpongTexture().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		// 1D FFT horizontal 
		for (int i=0; i<log_2_N; i++)
		{	
			getButterflyShader().updateUniforms(pingpong, 0, i);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		 //1D FFT vertical 
		for (int j=0; j<log_2_N; j++)
		{
			getButterflyShader().updateUniforms(pingpong, 1, j);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		getInversionShader().bind();
		getInversionShader().updateUniforms(N,pingpong);
		glBindImageTexture(0, heightmap.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		heightmap.bind();
		heightmap.trilinearFilter();
	}

	public float getT(){
		return t;
	}

}
