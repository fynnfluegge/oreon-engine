package engine.renderer.terrain.fractals;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.util.Random;

import engine.core.Texture;
import engine.gpgpu.fastFourierTransform.FastFourierTransform;
import engine.math.Vec2f;
import engine.shaderprograms.fft.ButterflyShader;
import engine.shaderprograms.fft.InversionShader;

public class FractalFFT extends FastFourierTransform{

	private Texture heightmap;
	
	public FractalFFT(int N, int L, float A, float v, Vec2f w, float l) {
		
		super(N);
		t = new Random().nextInt(1000);
		FractalFourierComponents components = new FractalFourierComponents(N, L, A, v, w, l);
		setFourierComponents(components);
		setButterflyShader(ButterflyShader.getInstance());
		setInversionShader(InversionShader.getInstance());
		
		heightmap = new Texture();
		heightmap.generate();
		heightmap.bind();
		glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(N)/Math.log(2)), GL_RGBA32F, N, N);
		
		setPingpongTexture(new Texture());
		getPingpongTexture().generate();
		getPingpongTexture().bind();
		getPingpongTexture().noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
	}

	public void render()
	{
		getFourierComponents().update(t);
		
		
		pingpong = 0;
		
		getButterflyShader().execute();
		
		glBindImageTexture(0, getTwiddles().getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, ((FractalFourierComponents) getFourierComponents()).getFourierComponents().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, getPingpongTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		// 1D FFT horizontal 
		for (int i=0; i<log_2_N; i++)
		{	
			getButterflyShader().sendUniforms(pingpong, 0, i);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		 //1D FFT vertical 
		for (int j=0; j<log_2_N; j++)
		{
			getButterflyShader().sendUniforms(pingpong, 1, j);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		getInversionShader().execute();
		getInversionShader().sendUniforms(N,pingpong);
		glBindImageTexture(0, heightmap.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		heightmap.bind();
		heightmap.mipmap();
	}

	public Texture getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(Texture heightmap) {
		this.heightmap = heightmap;
	}

}
