package org.oreon.modules.gl.gpgpu.fft;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.Texture2D;

public abstract class FastFourierTransform {
	
	protected int log_2_N;
	protected int pingpong;
	protected int N;
	protected float t;
	protected float t_delta;
	private GLShaderProgram butterflyShader;
	private GLShaderProgram inversionShader;
	private Texture2D pingpongTexture;
	private TwiddleFactors twiddles;
	private FourierComponents fourierComponents;
	
	protected FastFourierTransform(int N){
		this.N = N;
		log_2_N =  (int) (Math.log(N)/Math.log(2));
		twiddles = new TwiddleFactors(N);
	}
	
	public void init()
	{
		fourierComponents.getSpectrum().render();
		twiddles.render();
	}
	
	public abstract void render();

	public int getN() {
		return N;
	}

	public int getLog_2_N() {
		return log_2_N;
	}

	public void setLog_2_N(int log_2_N) {
		this.log_2_N = log_2_N;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getT_delta() {
		return t_delta;
	}

	public void setT_delta(float t_delta) {
		this.t_delta = t_delta;
	}

	public GLShaderProgram getButterflyShader() {
		return butterflyShader;
	}

	public void setButterflyShader(GLShaderProgram butterflyShader) {
		this.butterflyShader = butterflyShader;
	}

	public int getPingpong() {
		return pingpong;
	}

	public void setPingpong(int pingpong) {
		this.pingpong = pingpong;
	}

	public TwiddleFactors getTwiddles() {
		return twiddles;
	}

	public void setTwiddles(TwiddleFactors twiddles) {
		this.twiddles = twiddles;
	}

	public FourierComponents getFourierComponents() {
		return fourierComponents;
	}

	public void setFourierComponents(FourierComponents fourierComponents) {
		this.fourierComponents = fourierComponents;
	}

	public GLShaderProgram getInversionShader() {
		return inversionShader;
	}

	public void setInversionShader(GLShaderProgram inversionShader) {
		this.inversionShader = inversionShader;
	}

	public Texture2D getPingpongTexture() {
		return pingpongTexture;
	}

	public void setPingpongTexture(Texture2D pingpongTexture) {
		this.pingpongTexture = pingpongTexture;
	}
	
}
