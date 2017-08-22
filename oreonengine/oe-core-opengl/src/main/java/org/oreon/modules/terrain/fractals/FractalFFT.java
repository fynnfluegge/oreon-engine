package org.oreon.modules.terrain.fractals;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.math.Vec2f;
import org.oreon.core.shaders.computing.FFTButterflyShader;
import org.oreon.core.shaders.computing.FFTInversionShader;
import org.oreon.modules.gpgpu.fft.FastFourierTransform;

public class FractalFFT extends FastFourierTransform{

	private Texture2D heightmap;
	
	public FractalFFT(int N, int L, float A, float v, Vec2f w, float l) {
			
		super(N);
		
		setFourierComponents(new FractalFourierComponents(N, L, A, v, w, l));
		setButterflyShader(FFTButterflyShader.getInstance());
		setInversionShader(FFTInversionShader.getInstance());
		heightmap = new Texture2D();
		heightmap.generate();
		heightmap.bind();
		heightmap.trilinearFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		setPingpongTexture(new Texture2D());
		getPingpongTexture().generate();
		getPingpongTexture().bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);	
	}

	public void render()
	{
		getFourierComponents().update(t);
		
		pingpong = 0;
		
		getButterflyShader().bind();
		
		glBindImageTexture(0, getTwiddles().getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, ((FractalFourierComponents) getFourierComponents()).getFourierComponents().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
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
		glBindImageTexture(0, heightmap.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		heightmap.bind();
		heightmap.trilinearFilter();
	}

	public Texture2D getHeightmap() {
		return heightmap;
	}

	public float getT(){
		return t;
	}

}
