package engine.renderer.water;

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
import engine.core.Texture;
import engine.math.Vec2f;
import engine.shaderprograms.water.PhillipsSpectrumShader;

public class PhillipsSpectrum {
	
	private int N;
	private int L;
	private Vec2f w = new Vec2f(1,0).normalize();
	private float g = 9.81f;
	private float velocity = 30;
	private float l = (velocity*velocity)/g;
	private float A = 20f;
	private PhillipsSpectrumShader shader;
	private Texture noise0;
	private Texture noise1;
	private Texture noise2;
	private Texture noise3;
	private Texture h0k;
	private Texture h0kminus;
	
	public PhillipsSpectrum(int N, int L)
	{
		this.N = N;
		this.L = L;
			
		shader = PhillipsSpectrumShader.getInstance();
		
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
		
		noise0 = new Texture("./res/textures/Noise/Noise0.jpg");
		noise0.bind();
		noise0.noFilter();
		noise1 = new Texture("./res/textures/Noise/Noise1.jpg");
		noise1.bind();
		noise1.noFilter();
		noise2 = new Texture("./res/textures/Noise/Noise2.jpg");
		noise2.bind();
		noise2.noFilter();
		noise3 = new Texture("./res/textures/Noise/Noise3.jpg");
		noise3.bind();
		noise3.noFilter();
	}
	
	public void renderToTexture()
	{
		shader.execute();
		shader.sendUniforms(N, L, A, w, l);
		
		glActiveTexture(GL_TEXTURE0);
		noise0.bind();
		
		glActiveTexture(GL_TEXTURE1);
		noise1.bind();
		
		glActiveTexture(GL_TEXTURE2);
		noise2.bind();
		
		glActiveTexture(GL_TEXTURE3);
		noise3.bind();
		
		shader.sendUniforms(0, 1, 2, 3);
		
		glBindImageTexture(0, h0k.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glBindImageTexture(1, h0kminus.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glDispatchCompute(N/16,N/16,1);	
	}
	

	public Texture geth0kminus() {
		return h0kminus;
	}

	public Texture geth0k() {
		return h0k;
	}

}
