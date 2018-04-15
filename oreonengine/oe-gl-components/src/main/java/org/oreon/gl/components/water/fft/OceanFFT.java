package org.oreon.gl.components.water.fft;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;
import org.oreon.gl.components.gpgpu.fft.FFTButterflyShader;
import org.oreon.gl.components.gpgpu.fft.FFTInversionShader;
import org.oreon.gl.components.gpgpu.fft.FastFourierTransform;

import lombok.Getter;

public class OceanFFT extends FastFourierTransform{
	
	@Getter
	private GLTexture Dy;
	@Getter
	private GLTexture Dx;
	@Getter
	private GLTexture Dz;
	private boolean choppy;
		
	public OceanFFT(int N)
	{
		super(N);
		
		int L = 1000;
		
		setFourierComponents(new Tilde_hkt(N,L));
		setButterflyShader(FFTButterflyShader.getInstance());
		setInversionShader(FFTInversionShader.getInstance());
		
		Dy = new Texture2DStorageRGBA32F(N,N,1);
		
		Dx = new Texture2DStorageRGBA32F(N,N,1);;
		
		Dz = new Texture2DStorageRGBA32F(N,N,1);
		
		setPingpongTexture(new Texture2DStorageRGBA32F(N,N,1));
	}
		
	public void render()
	{
		getFourierComponents().update(t);
		
		// Dy-FFT
		
		pingpong = 0;
		
		getButterflyShader().bind();
		
		glBindImageTexture(0, getTwiddles().getTexture().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDyComponents().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
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
		glBindImageTexture(0, Dy.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		
		
		if (isChoppy()){
			
			// Dx-FFT
			
			pingpong = 0;
					
			getButterflyShader().bind();
			
			glBindImageTexture(0, getTwiddles().getTexture().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDxComponents().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
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
			getInversionShader().updateUniforms(getN(),pingpong);
			glBindImageTexture(0, Dx.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
		
			// Dz-FFT
							
			pingpong = 0;
							
			getButterflyShader().bind();
			
			glBindImageTexture(0, getTwiddles().getTexture().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDzComponents().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
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
			glBindImageTexture(0, Dz.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
		}
			
		t += t_delta;
	}

	public boolean isChoppy() {
		return choppy;
	}

	public void setChoppy(boolean choppy) {
		this.choppy = choppy;
	}
}
