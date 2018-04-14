package org.oreon.gl.components.gpgpu.fft;

import org.oreon.core.gl.pipeline.GLShaderProgram;


public abstract class FourierComponents {
	
	protected int N;
	protected int L;
	private FourierSpectrum spectrum;
	private GLShaderProgram shader;
	
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

	public GLShaderProgram getShader() {
		return shader;
	}

	public void setShader(GLShaderProgram shader) {
		this.shader = shader;
	}
	
}
