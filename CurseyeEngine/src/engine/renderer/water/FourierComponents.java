package engine.renderer.water;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import engine.core.Texture;
import engine.shaderprograms.water.FourierComponentsShader;

public class FourierComponents {
	
	private int N;
	private int L;
	private FourierComponentsShader shader;
	private PhillipsSpectrum phillipsComponents;
	private Texture pingpong0Dy;
	private Texture pingpong0Dx;
	private Texture pingpong0Dz;
	
	public FourierComponents(int N, int L)
	{
		this.N = N;
		this.L = L;
		
		phillipsComponents = new PhillipsSpectrum(N,L);
		
		shader = FourierComponentsShader.getInstance();
		
		pingpong0Dy = new Texture();
		pingpong0Dy.generate();
		pingpong0Dy.bind();
		pingpong0Dy.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		pingpong0Dx = new Texture();
		pingpong0Dx.generate();
		pingpong0Dx.bind();
		pingpong0Dx.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		pingpong0Dz = new Texture();
		pingpong0Dz.generate();
		pingpong0Dz.bind();
		pingpong0Dz.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, N, N);
		
		phillipsComponents.renderToTexture();
	}
	
	public void update(float t)
	{
		shader.execute();
		shader.sendUniforms(L,N,t);
		glBindImageTexture(0, pingpong0Dy.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(1, pingpong0Dx.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(2, pingpong0Dz.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(3, phillipsComponents.geth0k().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glBindImageTexture(4, phillipsComponents.geth0kminus().getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);	
		glFinish();
	}

	public Texture getPingpong0Dy() {
		return pingpong0Dy;
	}
	
	public Texture getPingpong0Dx() {
		return pingpong0Dx;
	}
	
	public Texture getPingpong0Dz() {
		return pingpong0Dz;
	}
}
