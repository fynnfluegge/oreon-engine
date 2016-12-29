package modules.water.fft;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL11.glFinish;

import engine.core.Input;
import engine.shader.computing.FFTButterflyShader;
import engine.shader.computing.FFTInversionShader;
import engine.textures.Texture2D;
import modules.gpgpu.fft.FastFourierTransform;


public class OceanFFT extends FastFourierTransform{
		
	private Texture2D Dy;
	private Texture2D Dx;
	private Texture2D Dz;
		
		public OceanFFT(int N)
		{
			super(N);
			
			t_delta = 0.035f;
			int L = 1000;
			
			setFourierComponents(new Tilde_hkt(N,L));
			setButterflyShader(FFTButterflyShader.getInstance());
			setInversionShader(FFTInversionShader.getInstance());
			
			Dy = new Texture2D();
			Dy.generate();
			Dy.bind();
			glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
			
			Dx = new Texture2D();
			Dx.generate();
			Dx.bind();
			glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
			
			Dz = new Texture2D();
			Dz.generate();
			Dz.bind();
			glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
			
			setPingpongTexture(new Texture2D());
			getPingpongTexture().generate();
			getPingpongTexture().bind();
			glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		}
		
		public void render()
		{
			getFourierComponents().update(t);
			
//			// Dy-FFT
//			
			pingpong = 0;
			
			getButterflyShader().bind();
			
			glBindImageTexture(0, getTwiddles().getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDyComponents().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(2, getPingpongTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			
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
			glBindImageTexture(0, Dy.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			
			// Dx-FFT
					
			pingpong = 0;
					
			getButterflyShader().bind();
			
			glBindImageTexture(0, getTwiddles().getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDxComponents().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(2, getPingpongTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
					
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
			glBindImageTexture(0, Dx.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			
			// Dz-FFT
							
			pingpong = 0;
							
			getButterflyShader().bind();
			
			glBindImageTexture(0, getTwiddles().getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(1, ((Tilde_hkt) getFourierComponents()).getDzComponents().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glBindImageTexture(2, getPingpongTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
							
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
			glBindImageTexture(0, Dz.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
				
			if(!Input.isPause())
				t += t_delta;
		}

		public Texture2D getDy() {
			return Dy;
		}

		public Texture2D getDx() {
			return Dx;
		}

		public Texture2D getDz() {
			return Dz;
		}
}
