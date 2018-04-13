package org.oreon.modules.gl.gpgpu.fft;

import org.oreon.core.gl.pipeline.GLShaderProgram;


public abstract class FourierSpectrum {
	
	protected int N;
	protected int L;
	private GLShaderProgram shader;
	
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

	public GLShaderProgram getShader() {
		return shader;
	}

	public void setShader(GLShaderProgram shader) {
		this.shader = shader;
	}

	public int getL() {
		return L;
	}

	public void setL(int l) {
		L = l;
	}

}
