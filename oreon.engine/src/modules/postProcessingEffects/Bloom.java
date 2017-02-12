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
import engine.shader.bloom.BloomBlurAdditiveBlendShader;
import engine.shader.bloom.BloomBlurSceneShader;
import engine.shader.bloom.HorizontalBloomBlurShader;
import engine.shader.bloom.VerticalBloomBlurShader;
import engine.textures.Texture2D;

public class Bloom {

	private Texture2D bloomTexture;
	private Texture2D horizontalBloomBlurTexture_div2;
	private Texture2D verticalBloomBlurTexture_div2;
	private Texture2D horizontalBloomBlurTexture_div4;
	private Texture2D verticalBloomBlurTexture_div4;
	private Texture2D horizontalBloomBlurTexture_div8;
	private Texture2D verticalBloomBlurTexture_div8;
	private Texture2D horizontalBloomBlurTexture_div16;
	private Texture2D verticalBloomBlurTexture_div16;

	private Texture2D additiveBlendBloomTexture;
	private Texture2D bloomBlurSceneTexture;
	
	private BloomShader bloomShader;
	private HorizontalBloomBlurShader horizontalBlurShader;
	private VerticalBloomBlurShader verticalBlurShader;
	private BloomBlurSceneShader bloomBlurShader;
	private BloomBlurAdditiveBlendShader bloomBlurAdditiveBlendShader;
	
	public Bloom(){
		
		bloomShader = BloomShader.getInstance();
		bloomBlurAdditiveBlendShader = BloomBlurAdditiveBlendShader.getInstance();
		horizontalBlurShader = HorizontalBloomBlurShader.getInstance();
		verticalBlurShader = VerticalBloomBlurShader.getInstance();
		bloomBlurShader = BloomBlurSceneShader.getInstance();
		
		bloomTexture = new Texture2D();
		bloomTexture.generate();
		bloomTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		bloomTexture.bilinearFilter();
		bloomTexture.clampToEdge();
		
		additiveBlendBloomTexture = new Texture2D();
		additiveBlendBloomTexture.generate();
		additiveBlendBloomTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		additiveBlendBloomTexture.bilinearFilter();
		additiveBlendBloomTexture.clampToEdge();
		
		bloomBlurSceneTexture = new Texture2D();
		bloomBlurSceneTexture.generate();
		bloomBlurSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		bloomBlurSceneTexture.bilinearFilter();
		bloomBlurSceneTexture.clampToEdge();
		
		horizontalBloomBlurTexture_div2 = new Texture2D();
		horizontalBloomBlurTexture_div2.generate();
		horizontalBloomBlurTexture_div2.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/2,
						Window.getInstance().getHeight()/2,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBloomBlurTexture_div2.bilinearFilter();
		horizontalBloomBlurTexture_div2.clampToEdge();
		
		verticalBloomBlurTexture_div2 = new Texture2D();
		verticalBloomBlurTexture_div2.generate();
		verticalBloomBlurTexture_div2.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/2,
						Window.getInstance().getHeight()/2,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBloomBlurTexture_div2.bilinearFilter();
		verticalBloomBlurTexture_div2.clampToEdge();
		
		horizontalBloomBlurTexture_div4 = new Texture2D();
		horizontalBloomBlurTexture_div4.generate();
		horizontalBloomBlurTexture_div4.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/4,
						Window.getInstance().getHeight()/4,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBloomBlurTexture_div4.bilinearFilter();
		horizontalBloomBlurTexture_div4.clampToEdge();
		
		verticalBloomBlurTexture_div4 = new Texture2D();
		verticalBloomBlurTexture_div4.generate();
		verticalBloomBlurTexture_div4.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/4,
						Window.getInstance().getHeight()/4,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBloomBlurTexture_div4.bilinearFilter();
		verticalBloomBlurTexture_div4.clampToEdge();
		
		horizontalBloomBlurTexture_div8 = new Texture2D();
		horizontalBloomBlurTexture_div8.generate();
		horizontalBloomBlurTexture_div8.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/8,
						Window.getInstance().getHeight()/8,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBloomBlurTexture_div8.bilinearFilter();
		horizontalBloomBlurTexture_div8.clampToEdge();
		
		verticalBloomBlurTexture_div8 = new Texture2D();
		verticalBloomBlurTexture_div8.generate();
		verticalBloomBlurTexture_div8.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/8,
						Window.getInstance().getHeight()/8,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBloomBlurTexture_div8.bilinearFilter();
		verticalBloomBlurTexture_div8.clampToEdge();
		
		horizontalBloomBlurTexture_div16 = new Texture2D();
		horizontalBloomBlurTexture_div16.generate();
		horizontalBloomBlurTexture_div16.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/16,
						Window.getInstance().getHeight()/16,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBloomBlurTexture_div16.bilinearFilter();
		horizontalBloomBlurTexture_div16.clampToEdge();
		
		verticalBloomBlurTexture_div16 = new Texture2D();
		verticalBloomBlurTexture_div16.generate();
		verticalBloomBlurTexture_div16.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth()/16,
						Window.getInstance().getHeight()/16,
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBloomBlurTexture_div16.bilinearFilter();
		verticalBloomBlurTexture_div16.clampToEdge();
	}
	
	public void render(Texture2D sceneSampler) {
				
		bloomShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, bloomTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div2.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2);
		glDispatchCompute(Window.getInstance().getWidth()/16, Window.getInstance().getHeight()/16, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div2.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div2.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/16, Window.getInstance().getHeight()/16, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div4.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, Window.getInstance().getWidth()/4, Window.getInstance().getHeight()/4);
		glDispatchCompute(Window.getInstance().getWidth()/32, Window.getInstance().getHeight()/32, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div4.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div4.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/32, Window.getInstance().getHeight()/32, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div8.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8);
		glDispatchCompute(Window.getInstance().getWidth()/64, Window.getInstance().getHeight()/64, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div8.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div8.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/64, Window.getInstance().getHeight()/64, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div16.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(bloomTexture, Window.getInstance().getWidth()/16, Window.getInstance().getHeight()/16);
		glDispatchCompute(Window.getInstance().getWidth()/128, Window.getInstance().getHeight()/128, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div16.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurTexture_div16.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/128, Window.getInstance().getHeight()/128, 1);	
		glFinish();
		
		bloomBlurAdditiveBlendShader.bind();
		glBindImageTexture(0, additiveBlendBloomTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		bloomBlurAdditiveBlendShader.updateUniforms(verticalBloomBlurTexture_div2,
													verticalBloomBlurTexture_div4,
													verticalBloomBlurTexture_div8,
													verticalBloomBlurTexture_div16,
													Window.getInstance().getWidth(),
													Window.getInstance().getHeight());
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		bloomBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, additiveBlendBloomTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, bloomBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getBloomTexture() {
		return bloomTexture;
	}

	public Texture2D getBloomBlurSceneTexture() {
		return bloomBlurSceneTexture;
	}

	public Texture2D getHorizontalBloomBlurTexture_div2() {
		return horizontalBloomBlurTexture_div2;
	}

	public Texture2D getVerticalBloomBlurTexture_div2() {
		return verticalBloomBlurTexture_div2;
	}

	public Texture2D getHorizontalBloomBlurTexture_div4() {
		return horizontalBloomBlurTexture_div4;
	}

	public Texture2D getVerticalBloomBlurTexture_div4() {
		return verticalBloomBlurTexture_div4;
	}

	public Texture2D getHorizontalBloomBlurTexture_div8() {
		return horizontalBloomBlurTexture_div8;
	}
	
	public Texture2D getVerticalBloomBlurTexture_div8() {
		return verticalBloomBlurTexture_div8;
	}
	
	public Texture2D getHorizontalBloomBlurTexture_div16() {
		return horizontalBloomBlurTexture_div16;
	}

	public Texture2D getVerticalBloomBlurTexture_div16() {
		return verticalBloomBlurTexture_div16;
	}

	public Texture2D getAdditiveBlendBloomTexture() {
		return additiveBlendBloomTexture;
	}
}
