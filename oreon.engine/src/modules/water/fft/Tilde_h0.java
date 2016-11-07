package modules.water.fft;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import modules.fastFourierTransform.FourierSpectrum;
import engine.math.Vec2f;
import engine.shaders.water.Tilde_h0Shader;
import engine.textures.Texture;

public class Tilde_h0 extends FourierSpectrum{
	
	private Vec2f wind = new Vec2f(1,1).normalize();
	private float windspeed = 25;
	private float A = 2f;
	private Texture noise0;
	private Texture noise1;
	private Texture noise2;
	private Texture noise3;
	private Texture h0k;
	private Texture h0kminus;
	
	public Tilde_h0(int N, int L)
	{
		super(N,L);
		
		setShader(Tilde_h0Shader.getInstance());
		
		h0k = new Texture();
		h0k.generate();
		h0k.bind();
		h0k.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		h0kminus = new Texture();
		h0kminus.generate();
		h0kminus.bind();
		h0kminus.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		noise0 = new Texture("./res/textures/Noise/Noise" + N + "_0.jpg");
		noise0.bind();
		noise0.noFilter();
		noise1 = new Texture("./res/textures/Noise/Noise" + N + "_1.jpg");
		noise1.bind();
		noise1.noFilter();
		noise2 = new Texture("./res/textures/Noise/Noise" + N + "_2.jpg");
		noise2.bind();
		noise2.noFilter();
		noise3 = new Texture("./res/textures/Noise/Noise" + N + "_3.jpg");
		noise3.bind();
		noise3.noFilter();
	}
	
	@Override
	public void render() {
		
		getShader().bind();
		getShader().updateUniforms(getN(), L, A, wind, windspeed);
		
		glActiveTexture(GL_TEXTURE0);
		noise0.bind();
		
		glActiveTexture(GL_TEXTURE1);
		noise1.bind();
		
		glActiveTexture(GL_TEXTURE2);
		noise2.bind();
		
		glActiveTexture(GL_TEXTURE3);
		noise3.bind();
		
		getShader().updateUniforms(0, 1, 2, 3);
		
		glBindImageTexture(0, h0k.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glBindImageTexture(1, h0kminus.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glDispatchCompute(getN()/16,getN()/16,1);		
	}

	public Texture geth0kminus() {
		return h0kminus;
	}

	public Texture geth0k() {
		return h0k;
	}
}
