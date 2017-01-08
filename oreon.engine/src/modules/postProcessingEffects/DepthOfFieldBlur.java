package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import engine.buffers.Framebuffer;
import engine.core.Window;
import engine.shader.dofBlur.DepthOfFieldHorizontalBlurShader;
import engine.shader.dofBlur.DepthOfFieldVerticalBlurShader;
import engine.textures.Texture2D;

public class DepthOfFieldBlur {
	
	private Texture2D horizontalBlurTexture;
	private Texture2D DepthOfFieldBlurTexture;
	private DepthOfFieldHorizontalBlurShader horizontalShader;
	private DepthOfFieldVerticalBlurShader verticalShader;
	
	private Framebuffer largeBlurFbo;
	private Texture2D largeBlurSampler;
	private Framebuffer smallBlurFbo;
	private Texture2D smallBlurSampler;
	
	private float[] gaussianKernel_7 = {0.00598f,0.060626f,0.241843f,0.383103f,0.241843f,0.060626f,0.00598f};
	
	private boolean enabled = true;
	
	public DepthOfFieldBlur() {
		
		horizontalShader = DepthOfFieldHorizontalBlurShader.getInstance();
		verticalShader = DepthOfFieldVerticalBlurShader.getInstance();
		horizontalBlurTexture = new Texture2D();
		horizontalBlurTexture.generate();
		horizontalBlurTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		DepthOfFieldBlurTexture = new Texture2D();
		DepthOfFieldBlurTexture.generate();
		DepthOfFieldBlurTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		smallBlurSampler = new Texture2D();
		smallBlurSampler.generate();
		smallBlurSampler.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
						Window.getInstance().getWidth()/2,
						Window.getInstance().getHeight()/2,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		smallBlurSampler.bilinearFilter();
		smallBlurSampler.clampToEdge();
		
		smallBlurFbo = new Framebuffer();
		smallBlurFbo.bind();
		smallBlurFbo.createColorTextureAttachment(smallBlurSampler.getId(), 0);
		smallBlurFbo.checkStatus();
		smallBlurFbo.unbind();
		
		largeBlurSampler = new Texture2D();
		largeBlurSampler.generate();
		largeBlurSampler.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
						(int)(Window.getInstance().getWidth()/1.5f),
						(int)(Window.getInstance().getHeight()/1.5f),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		largeBlurSampler.bilinearFilter();
		largeBlurSampler.clampToEdge();
		
		largeBlurFbo = new Framebuffer();
		largeBlurFbo.bind();
		largeBlurFbo.createColorTextureAttachment(largeBlurSampler.getId(), 0);
		largeBlurFbo.checkStatus();
		largeBlurFbo.unbind();
	}
	
	public void render(Texture2D depthmap, Texture2D sceneSampler) {
		
		horizontalShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, smallBlurSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(2, largeBlurSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(3, horizontalBlurTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		horizontalShader.updateUniforms(depthmap, gaussianKernel_7);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		verticalShader.bind();
		glBindImageTexture(0, horizontalBlurTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, DepthOfFieldBlurTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		verticalShader.updateUniforms(depthmap, gaussianKernel_7);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Texture2D getHorizontalBlurTexture() {
		return horizontalBlurTexture;
	}

	public Texture2D getDepthOfFieldBlurTexture() {
		return DepthOfFieldBlurTexture;
	}

	public Framebuffer getLargeBlurFbo() {
		return largeBlurFbo;
	}

	public Texture2D getLargeBlurSampler() {
		return largeBlurSampler;
	}

	public Framebuffer getSmallBlurFbo() {
		return smallBlurFbo;
	}
	
	public Texture2D getSmallBlurSampler() {
		return smallBlurSampler;
	}
}
