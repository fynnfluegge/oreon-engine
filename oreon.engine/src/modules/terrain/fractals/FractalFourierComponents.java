package modules.terrain.fractals;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import engine.math.Vec2f;
import engine.shader.terrain.fractals.FractalFourierComponentsShader;
import engine.textures.Texture2D;
import modules.gpgpu.fft.FourierComponents;

public class FractalFourierComponents extends FourierComponents{

	private Texture2D fourierComponents;

	public FractalFourierComponents(int N, int L, float A, float v, Vec2f w, float l) {
		
		super(N,L);
		FractalSpectrum spectrum = new FractalSpectrum(N,L,A,v,w,l);
		setSpectrum(spectrum);
		setShader(FractalFourierComponentsShader.getInstance());
		
		fourierComponents = new Texture2D();
		fourierComponents.generate();
		fourierComponents.bind();
		fourierComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
	}
	
	@Override
	public void update(float t) {
		
		getShader().bind();
		getShader().updateUniforms(L,t);
		glBindImageTexture(0, fourierComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, ((FractalSpectrum)getSpectrum()).geth0k().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(4,  ((FractalSpectrum)getSpectrum()).geth0kminus().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);	
		glFinish();
	}

	public Texture2D getFourierComponents() {
		return fourierComponents;
	}

	public void setFourierComponents(Texture2D fourierComponents) {
		this.fourierComponents = fourierComponents;
	}
}
