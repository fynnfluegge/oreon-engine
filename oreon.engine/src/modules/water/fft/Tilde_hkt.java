package modules.water.fft;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import engine.shader.water.Tilde_hktShader;
import engine.textures.Texture2D;
import modules.gpgpu.fft.FourierComponents;

public class Tilde_hkt extends FourierComponents{
	
	private Texture2D dyComponents;
	private Texture2D dxComponents;
	private Texture2D dzComponents;

	public Tilde_hkt(int N, int L) {
		
		super(N, L);
		setSpectrum(new Tilde_h0(N,L));
		setShader(Tilde_hktShader.getInstance());
		
		dyComponents = new Texture2D();
		dyComponents.generate();
		dyComponents.bind();
		dyComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		dxComponents = new Texture2D();
		dxComponents.generate();
		dxComponents.bind();
		dxComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		dzComponents = new Texture2D();
		dzComponents.generate();
		dzComponents.bind();
		dzComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
	}
	
	@Override
	public void update(float t) {
		
		getShader().bind();
		getShader().updateUniforms(L,t);
		glBindImageTexture(0, dyComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, dxComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, dzComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, ((Tilde_h0)getSpectrum()).geth0k().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(4, ((Tilde_h0)getSpectrum()).geth0kminus().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(getN()/16,getN()/16,1);	
		glFinish();
	}

	public int getL() {
		return L;
	}

	public Texture2D getDyComponents() {
		return dyComponents;
	}

	public Texture2D getDxComponents() {
		return dxComponents;
	}
	public Texture2D getDzComponents() {
		return dzComponents;
	}
}
