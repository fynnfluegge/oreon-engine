package org.oreon.gl.components.water.fft;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;
import org.oreon.gl.components.gpgpu.fft.FourierComponents;
import org.oreon.gl.components.water.shader.Tilde_hktShader;

import lombok.Getter;

@Getter
public class Tilde_hkt extends FourierComponents{
	
	private GLTexture dyComponents;
	private GLTexture dxComponents;
	private GLTexture dzComponents;

	public Tilde_hkt(int N, int L) {
		
		super(N, L);
		
		setSpectrum(new Tilde_h0(N,L));
		setShader(Tilde_hktShader.getInstance());
		
		dyComponents = new Texture2DStorageRGBA32F(N,N,1);
		dxComponents = new Texture2DStorageRGBA32F(N,N,1);
		dzComponents = new Texture2DStorageRGBA32F(N,N,1);

	}
	
	@Override
	public void update(float t) {
		
		getShader().bind();
		getShader().updateUniforms(L,N,t);
		glBindImageTexture(0, dyComponents.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, dxComponents.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, dzComponents.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, ((Tilde_h0)getSpectrum()).getH0k().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, ((Tilde_h0)getSpectrum()).getH0kminus().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glDispatchCompute(getN()/16,getN()/16,1);	
		glFinish();
	}

	public int getL() {
		return L;
	}

}
