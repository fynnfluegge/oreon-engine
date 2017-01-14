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

import engine.core.Window;
import engine.shader.bloom.BloomShader;
import engine.shader.bloom.BloomBlurShader;
import engine.shader.bloom.HorizontalBloomBlurShader;
import engine.shader.bloom.VerticalBloomBlurShader;
import engine.textures.Texture2D;

public class Bloom {

	private Texture2D bloomSceneTexture;
	private Texture2D horizontalBloomBlurSceneTexture;
	private Texture2D verticalBloomBlurSceneTexture;
	private Texture2D bloomBlurSceneTexture;
	
	private BloomShader bloomShader;
	private HorizontalBloomBlurShader horizontalBlurShader;
	private VerticalBloomBlurShader verticalBlurShader;
	private BloomBlurShader bloomBlurShader;
	
	
	public Bloom(){
		
		bloomShader = BloomShader.getInstance();
		horizontalBlurShader = HorizontalBloomBlurShader.getInstance();
		verticalBlurShader = VerticalBloomBlurShader.getInstance();
		bloomBlurShader = BloomBlurShader.getInstance();
		
		bloomSceneTexture = new Texture2D();
		bloomSceneTexture.generate();
		bloomSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		bloomSceneTexture.bilinearFilter();
		bloomSceneTexture.clampToEdge();
		
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
	}
	
	public void render(Texture2D sceneSampler) {
		
		bloomShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, bloomSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, bloomSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
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
	
	public Texture2D getBloomSceneTexture() {
		return bloomSceneTexture;
	}

	public Texture2D getHorizontalBloomBlurSceneTexture() {
		return horizontalBloomBlurSceneTexture;
	}

	public Texture2D getVerticalBloomBlurSceneTexture() {
		return verticalBloomBlurSceneTexture;
	}

	public Texture2D getBloomBLurSceneTexture() {
		return bloomBlurSceneTexture;
	}
}
