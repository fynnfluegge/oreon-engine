package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
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
	private DepthOfFieldHorizontalBlurShader horizontalBlurShader;
	private DepthOfFieldVerticalBlurShader verticalBlurShader;
	
	private Framebuffer lowResFbo;
	private Texture2D lowResSceneSampler;
		
	public DepthOfFieldBlur() {
		
		horizontalBlurShader = DepthOfFieldHorizontalBlurShader.getInstance();
		verticalBlurShader = DepthOfFieldVerticalBlurShader.getInstance();
		
		horizontalBlurSceneTexture = new Texture2D();
		horizontalBlurSceneTexture.generate();
		horizontalBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA16F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		verticalBlurSceneTexture = new Texture2D();
		verticalBlurSceneTexture.generate();
		verticalBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA16F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		lowResSceneSampler = new Texture2D();
		lowResSceneSampler.generate();
		lowResSceneSampler.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						(int)(Window.getInstance().getWidth()/1.4f),
						(int)(Window.getInstance().getHeight()/1.4f),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		lowResSceneSampler.bilinearFilter();
		lowResSceneSampler.clampToEdge();
		
		lowResFbo = new Framebuffer();
		lowResFbo.bind();
		lowResFbo.createColorTextureAttachment(lowResSceneSampler.getId(), 0);
		lowResFbo.checkStatus();
		lowResFbo.unbind();
	}
	
	public void render(Texture2D depthmap, Texture2D sceneSampler) {
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, lowResSceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, horizontalBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBlurSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}

	public Texture2D getVerticalBlurSceneTexture() {
		return verticalBlurSceneTexture;
	}

	public Framebuffer getLowResFbo() {
		return lowResFbo;
	}
}
