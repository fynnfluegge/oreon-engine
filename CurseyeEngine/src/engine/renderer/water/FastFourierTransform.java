package engine.renderer.water;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL11.glFinish;
import engine.core.Input;
import engine.core.Texture;
import engine.renderpipeline.shaderPrograms.NormalMapShader;
import engine.renderpipeline.shaderPrograms.ocean.DisplacementMapShader;
import engine.renderpipeline.shaderPrograms.ocean.FFTShader;


public class FastFourierTransform {
	
	private int N = 256;
	private int L = 1000;
	private int log_2_N;
	private float t = 0;
	private float t_delta = 0.07f;
	private FFTShader fftShader;
	private DisplacementMapShader dspShader;
	private NormalMapShader nrmShader;
	private int pingpong;
	private Texture pingpong1;
	private Texture normalmap;
	private Texture Dy;
	private Texture Dx;
	private Texture Dz;
	private float normalstrength;
	
	private FourierComponents fourierComponents;
	private PhillipsSpectrum phillipsComponents;
	private TwiddleFactors twiddlesIndices;
	
	public FastFourierTransform()
	{
		log_2_N = (int) (Math.log(N)/Math.log(2));
		phillipsComponents = new PhillipsSpectrum(N,L);
		fourierComponents = new FourierComponents(N,L);
		twiddlesIndices = new TwiddleFactors(N);
		
		fftShader = FFTShader.getInstance();
		dspShader = DisplacementMapShader.getInstance();
		nrmShader = NormalMapShader.getInstance();
		
		Dy = new Texture();
		Dy.generate();
		Dy.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		Dx = new Texture();
		Dx.generate();
		Dx.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		Dz = new Texture();
		Dz.generate();
		Dz.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		normalmap = new Texture();
		normalmap.generate();
		normalmap.bind();
		glTexStorage2D(GL_TEXTURE_2D, log_2_N, GL_RGBA32F, N, N);

		pingpong1 = new Texture();
		pingpong1.generate();
		pingpong1.bind();
		pingpong1.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
	}
	
	public void init()
	{
		phillipsComponents.renderToTexture();
		twiddlesIndices.renderToTexture();
	}
	
	public void render()
	{
		fourierComponents.update(t);
		
		// Dy-FFT
		
		pingpong = 0;
		
		fftShader.execute();
		
		glBindImageTexture(0, twiddlesIndices.getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, fourierComponents.getPingpong0Dy().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, pingpong1.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		// 1D FFT horizontal 
		for (int i=0; i<log_2_N; i++)
		{	
			fftShader.sendUniforms(pingpong, 0, i);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		 //1D FFT vertical 
		for (int j=0; j<log_2_N; j++)
		{
			fftShader.sendUniforms(pingpong, 1, j);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
		
		dspShader.execute();
		dspShader.sendUniforms(N,pingpong);
		glBindImageTexture(0, Dy.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		
		// Dx-FFT
				
		pingpong = 0;
				
		fftShader.execute();
		
		glBindImageTexture(0, twiddlesIndices.getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, fourierComponents.getPingpong0Dx().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, pingpong1.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
				
		// 1D FFT horizontal 
		for (int i=0; i<log_2_N; i++)
		{	
			fftShader.sendUniforms(pingpong, 0, i);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
				
		//1D FFT vertical 
		for (int j=0; j<log_2_N; j++)
		{
			fftShader.sendUniforms(pingpong, 1, j);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
				
		dspShader.execute();
		dspShader.sendUniforms(N,pingpong);
		glBindImageTexture(0, Dx.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);	
		glFinish();
		
		// Dz-FFT
						
		pingpong = 0;
						
		fftShader.execute();
		
		glBindImageTexture(0, twiddlesIndices.getTexture().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, fourierComponents.getPingpong0Dz().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, pingpong1.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
						
		// 1D FFT horizontal 
		for (int i=0; i<log_2_N; i++)
		{	
			fftShader.sendUniforms(pingpong, 0, i);
			glDispatchCompute(N/16,N/16,1);	
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
						
		//1D FFT vertical 
		for (int j=0; j<log_2_N; j++)
		{
			fftShader.sendUniforms(pingpong, 1, j);
			glDispatchCompute(N/16,N/16,1);
			glFinish();
			pingpong++;
			pingpong %= 2;
		}
						
		dspShader.execute();
		dspShader.sendUniforms(N,pingpong);
		glBindImageTexture(0, Dz.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
			
		if(!Input.isPause())
			t += t_delta;
	}
	
	public void renderNormalmap()
	{
		nrmShader.execute();
		nrmShader.sendUniforms(Dy, N, normalstrength);
		glBindImageTexture(0, normalmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();

		normalmap.bind();
		normalmap.mipmap();
	}
	
	public Texture getNormalmap()
	{
		return normalmap;
	}

	public Texture getDy() {
		return Dy;
	}

	public Texture getDx() {
		return Dx;
	}

	public Texture getDz() {
		return Dz;
	}

	public float getNormalstrength() {
		return normalstrength;
	}

	public void setNormalstrength(float normalstrength) {
		this.normalstrength = normalstrength;
	}	
	
}
