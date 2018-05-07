package org.oreon.gl.components.fft;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;

import lombok.Getter;

public class Hkt {

	@Getter
	private GLTexture imageDyCoeficcients;
	@Getter
	private GLTexture imageDxCoefficients;
	@Getter
	private GLTexture imageDzCoefficients;
	
	private GLTexture imageH0k;
	private GLTexture imageH0minusK;
	
	private int N;
	private int L;
	private HktShader shader;
	
	public Hkt(int N, int L) {

		this.N = N;
		this.L = L;

		shader = HktShader.getInstance();
		
		imageDyCoeficcients = new Texture2DStorageRGBA32F(N,N,1);
		imageDxCoefficients = new Texture2DStorageRGBA32F(N,N,1);
		imageDzCoefficients = new Texture2DStorageRGBA32F(N,N,1);

	}
	
	public void render(float t) {
		
		shader.bind();
		shader.updateUniforms(L,N,t);
		glBindImageTexture(0, imageDyCoeficcients.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, imageDxCoefficients.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, imageDzCoefficients.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, imageH0k.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, imageH0minusK.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);	
		glFinish();
	}
}
