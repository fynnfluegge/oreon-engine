package org.oreon.modules.gpgpu.fft;

import org.oreon.core.gl.shaders.GLShader;


public abstract class FourierSpectrum {
	
	protected int N;
	protected int L;
	private GLShader shader;
	
	protected FourierSpectrum(int N, int L){
		this.N = N;
		this.L = L;
	}
	
	public abstract void render();


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

	public int getL() {
		return L;
	}

	public void setL(int l) {
		L = l;
	}

}
