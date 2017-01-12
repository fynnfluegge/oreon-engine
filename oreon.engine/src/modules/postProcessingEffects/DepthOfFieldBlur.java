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
	
	private Texture2D horizontalBlurSceneTexture;
	private Texture2D verticalBlurSceneTexture;
	private DepthOfFieldHorizontalBlurShader horizontalShader;
	private DepthOfFieldVerticalBlurShader verticalShader;
	
	private Framebuffer largeBlurFbo;
	private Texture2D largeBlurSceneSampler;
	private Framebuffer smallBlurFbo;
	private Texture2D smallBlurSceneSampler;
	
	private float[] gaussianKernel_7 = {0.00598f,0.060626f,0.241843f,0.383103f,0.241843f,0.060626f,0.00598f};
	
	public DepthOfFieldBlur() {
		
		horizontalShader = DepthOfFieldHorizontalBlurShader.getInstance();
		verticalShader = DepthOfFieldVerticalBlurShader.getInstance();
		horizontalBlurSceneTexture = new Texture2D();
		horizontalBlurSceneTexture.generate();
		horizontalBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		verticalBlurSceneTexture = new Texture2D();
		verticalBlurSceneTexture.generate();
		verticalBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		smallBlurSceneSampler = new Texture2D();
		smallBlurSceneSampler.generate();
		smallBlurSceneSampler.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
						Window.getInstance().getWidth()/2,
						Window.getInstance().getHeight()/2,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		smallBlurSceneSampler.bilinearFilter();
		smallBlurSceneSampler.clampToEdge();
		
		smallBlurFbo = new Framebuffer();
		smallBlurFbo.bind();
		smallBlurFbo.createColorTextureAttachment(smallBlurSceneSampler.getId(), 0);
		smallBlurFbo.checkStatus();
		smallBlurFbo.unbind();
		
		largeBlurSceneSampler = new Texture2D();
		largeBlurSceneSampler.generate();
		largeBlurSceneSampler.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
						(int)(Window.getInstance().getWidth()/1.2f),
						(int)(Window.getInstance().getHeight()/1.2f),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		largeBlurSceneSampler.bilinearFilter();
		largeBlurSceneSampler.clampToEdge();
		
		largeBlurFbo = new Framebuffer();
		largeBlurFbo.bind();
		largeBlurFbo.createColorTextureAttachment(largeBlurSceneSampler.getId(), 0);
		largeBlurFbo.checkStatus();
		largeBlurFbo.unbind();
	}
	
	public void render(Texture2D depthmap, Texture2D sceneSampler) {
		
		horizontalShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, smallBlurSceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(2, largeBlurSceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(3, horizontalBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		horizontalShader.updateUniforms(depthmap, gaussianKernel_7);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		verticalShader.bind();
		glBindImageTexture(0, horizontalBlurSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, verticalBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		verticalShader.updateUniforms(depthmap, gaussianKernel_7);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getHorizontalBlurSceneTexture() {
		return horizontalBlurSceneTexture;
	}

	public Texture2D getVerticalBlurSceneTexture() {
		return verticalBlurSceneTexture;
	}

	public Framebuffer getLargeBlurFbo() {
		return largeBlurFbo;
	}

	public Texture2D getLargeBlurSceneSampler() {
		return largeBlurSceneSampler;
	}

	public Framebuffer getSmallBlurFbo() {
		return smallBlurFbo;
	}
	
	public Texture2D getSmallBlurSceneSampler() {
		return smallBlurSceneSampler;
	}
}
