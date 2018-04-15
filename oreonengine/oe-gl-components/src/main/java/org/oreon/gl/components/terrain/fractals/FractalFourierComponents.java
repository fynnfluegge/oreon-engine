package org.oreon.gl.components.terrain.fractals;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.gpgpu.fft.FourierComponents;

import lombok.Getter;

public class FractalFourierComponents extends FourierComponents{

	@Getter
	private GLTexture fourierComponents;

	public FractalFourierComponents(int N, int L, float A, float v, Vec2f w, float l) {
		
		super(N,L);
		
		FractalSpectrum spectrum = new FractalSpectrum(N,L,A,v,w,l);
		setSpectrum(spectrum);
		setShader(FractalFourierComponentsShader.getInstance());
		fourierComponents = new Texture2DStorageRGBA32F(N,N,1);
	}
	
	@Override
	public void update(float t) {
		
		getShader().bind();
		getShader().updateUniforms(N,L,t);
		glBindImageTexture(0, fourierComponents.getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, ((FractalSpectrum)getSpectrum()).getH0k().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(4,  ((FractalSpectrum)getSpectrum()).getH0kminus().getHandle(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);	
		glFinish();
	}

}
