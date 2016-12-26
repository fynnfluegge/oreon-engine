package modules.gpgpu.fft;

import engine.shader.Shader;


public abstract class FourierSpectrum {
	
	protected int N;
	protected int L;
	private Shader shader;
	
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

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public int getL() {
		return L;
	}

	public void setL(int l) {
		L = l;
	}

}
