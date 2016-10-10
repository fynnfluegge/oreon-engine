package modules.water.fft;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import modules.fastFourierTransform.FourierComponents;
import engine.shaders.water.Tilde_hktShader;
import engine.textures.Texture;

public class Tilde_hkt extends FourierComponents{
	
	private Texture dyComponents;
	private Texture dxComponents;
	private Texture dzComponents;

	public Tilde_hkt(int N, int L) {
		
		super(N, L);
		setSpectrum(new Tilde_h0(N,L));
		setShader(Tilde_hktShader.getInstance());
		
		dyComponents = new Texture();
		dyComponents.generate();
		dyComponents.bind();
		dyComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		dxComponents = new Texture();
		dxComponents.generate();
		dxComponents.bind();
		dxComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		dzComponents = new Texture();
		dzComponents.generate();
		dzComponents.bind();
		dzComponents.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
	}
	
	@Override
	public void update(float t) {
		
		getShader().bind();
		getShader().updateUniforms(L,getN(),t);
		glBindImageTexture(0, dyComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, dxComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, dzComponents.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, ((Tilde_h0)getSpectrum()).geth0k().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(4,  ((Tilde_h0)getSpectrum()).geth0kminus().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(getN()/16,getN()/16,1);	
		glFinish();
	}

	public int getL() {
		return L;
	}

	public void setL(int l) {
		L = l;
	}

	public Texture getDyComponents() {
		return dyComponents;
	}

	public void setDyComponents(Texture dyComponents) {
		this.dyComponents = dyComponents;
	}

	public Texture getDxComponents() {
		return dxComponents;
	}

	public void setDxComponents(Texture dxComponents) {
		this.dxComponents = dxComponents;
	}

	public Texture getDzComponents() {
		return dzComponents;
	}

	public void setDzComponents(Texture dzComponents) {
		this.dzComponents = dzComponents;
	}
}
