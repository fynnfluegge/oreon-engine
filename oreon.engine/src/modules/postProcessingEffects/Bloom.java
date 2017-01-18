package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;

import java.nio.ByteBuffer;

import engine.buffers.Framebuffer;
import engine.core.Window;
import engine.shader.bloom.BloomShader;
import engine.shader.bloom.BloomBlurShader;
import engine.shader.bloom.HorizontalBloomBlurShader;
import engine.shader.bloom.VerticalBloomBlurShader;
import engine.textures.Texture2D;

public class Bloom {

	private Texture2D bloomTexture;
	private Texture2D horizontalBloomBlurSceneTexture;
	private Texture2D verticalBloomBlurSceneTexture;
	private Texture2D bloomBlurSceneTexture;
	
	private BloomShader bloomShader;
	private HorizontalBloomBlurShader horizontalBlurShader;
	private VerticalBloomBlurShader verticalBlurShader;
	private BloomBlurShader bloomBlurShader;
	
	private Framebuffer fbo_div2;
	private Framebuffer fbo_div4;
	private Texture2D sceneSampler_div2;
	private Texture2D sceneSampler_div4;
	
	
	public Bloom(){
		
		bloomShader = BloomShader.getInstance();
		horizontalBlurShader = HorizontalBloomBlurShader.getInstance();
		verticalBlurShader = VerticalBloomBlurShader.getInstance();
		bloomBlurShader = BloomBlurShader.getInstance();
		
		bloomTexture = new Texture2D();
		bloomTexture.generate();
		bloomTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		bloomTexture.bilinearFilter();
		bloomTexture.clampToEdge();
		
		horizontalBloomBlurSceneTexture = new Texture2D();
		horizontalBloomBlurSceneTexture.generate();
		horizontalBloomBlurSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBloomBlurSceneTexture.bilinearFilter();
		horizontalBloomBlurSceneTexture.clampToEdge();
		
		verticalBloomBlurSceneTexture = new Texture2D();
		verticalBloomBlurSceneTexture.generate();
		verticalBloomBlurSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBloomBlurSceneTexture.bilinearFilter();
		verticalBloomBlurSceneTexture.clampToEdge();
		
		bloomBlurSceneTexture = new Texture2D();
		bloomBlurSceneTexture.generate();
		bloomBlurSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		bloomBlurSceneTexture.bilinearFilter();
		bloomBlurSceneTexture.clampToEdge();
		
		sceneSampler_div2 = new Texture2D();
		sceneSampler_div2.generate();
		sceneSampler_div2.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/2,
						Window.getInstance().getHeight()/2,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sceneSampler_div2.bilinearFilter();
		sceneSampler_div2.clampToEdge();
		
		fbo_div2 = new Framebuffer();
		fbo_div2.bind();
		fbo_div2.createColorTextureAttachment(sceneSampler_div2.getId(), 0);
		fbo_div2.checkStatus();
		fbo_div2.unbind();
		
		sceneSampler_div4 = new Texture2D();
		sceneSampler_div4.generate();
		sceneSampler_div4.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						(int)(Window.getInstance().getWidth()/4f),
						(int)(Window.getInstance().getHeight()/4f),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sceneSampler_div4.bilinearFilter();
		sceneSampler_div4.clampToEdge();
		
		fbo_div4 = new Framebuffer();
		fbo_div4.bind();
		fbo_div4.createColorTextureAttachment(sceneSampler_div4.getId(), 0);
		fbo_div4.checkStatus();
		fbo_div4.unbind();
	}
	
	public void render(Texture2D sceneSampler) {
		
		bloomShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, bloomTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, bloomTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, horizontalBloomBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		bloomBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, bloomBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getBloomTexture() {
		return bloomTexture;
	}

	public Texture2D getHorizontalBloomBlurSceneTexture() {
		return horizontalBloomBlurSceneTexture;
	}

	public Texture2D getVerticalBloomBlurSceneTexture() {
		return verticalBloomBlurSceneTexture;
	}

	public Texture2D getBloomBlurSceneTexture() {
		return bloomBlurSceneTexture;
	}

	public Framebuffer getFbo_div2() {
		return fbo_div2;
	}

	public Framebuffer getFbo_div4() {
		return fbo_div4;
	}
}
