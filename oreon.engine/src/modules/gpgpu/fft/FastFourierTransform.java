package modules.gpgpu.fft;

import engine.shader.Shader;
import engine.textures.Texture2D;



public abstract class FastFourierTransform {
	
	protected int log_2_N;
	protected int pingpong;
	protected int N;
	protected float t;
	protected float t_delta;
	private Shader butterflyShader;
	private Shader inversionShader;
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

	public Shader getButterflyShader() {
		return butterflyShader;
	}

	public void setButterflyShader(Shader butterflyShader) {
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

	public Shader getInversionShader() {
		return inversionShader;
	}

	public void setInversionShader(Shader inversionShader) {
		this.inversionShader = inversionShader;
	}

	public Texture2D getPingpongTexture() {
		return pingpongTexture;
	}

	public void setPingpongTexture(Texture2D pingpongTexture) {
		this.pingpongTexture = pingpongTexture;
	}
	
}
