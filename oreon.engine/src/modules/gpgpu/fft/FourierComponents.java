package modules.gpgpu.fft;

import engine.shader.Shader;


public abstract class FourierComponents {
	
	protected int N;
	protected int L;
	private FourierSpectrum spectrum;
	private Shader shader;
	
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

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}
	
}
