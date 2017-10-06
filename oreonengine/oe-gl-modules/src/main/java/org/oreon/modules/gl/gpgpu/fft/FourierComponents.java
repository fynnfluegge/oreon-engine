package org.oreon.modules.gl.gpgpu.fft;

import org.oreon.core.gl.shaders.GLShader;


public abstract class FourierComponents {
	
	protected int N;
	protected int L;
	private FourierSpectrum spectrum;
	private GLShader shader;
	
	protected FourierComponents(int N, int L)
	{
		this.N = N;
		this.L = L;
	}
	
	public abstract void update(float t);


	public FourierSpectrum getSpectrum() {
		return spectrum;
	}

	public void setSpectrum(FourierSpectrum spectrum) {
		this.spectrum = spectrum;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public GLShader getShader() {
		return shader;
	}

	public void setShader(GLShader shader) {
		this.shader = shader;
	}
	
}
