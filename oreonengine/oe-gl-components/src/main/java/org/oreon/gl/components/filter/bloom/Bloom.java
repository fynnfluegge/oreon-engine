package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA16F;

import lombok.Getter;

@Getter
public class Bloom {

	@Getter
	private GLTexture bloomSceneTexture;
	
	private GLTexture sceneBrightnessTexture;
	private GLTexture horizontalBloomBlurTexture_div2;
	private GLTexture verticalBloomBlurTexture_div2;
	private GLTexture horizontalBloomBlurTexture_div4;
	private GLTexture verticalBloomBlurTexture_div4;
	private GLTexture horizontalBloomBlurTexture_div8;
	private GLTexture verticalBloomBlurTexture_div8;
	private GLTexture horizontalBloomBlurTexture_div12;
	private GLTexture verticalBloomBlurTexture_div12;
	private GLTexture additiveBlendBloomTexture;
	
	private SceneBrightnessShader sceneBrightnessShader;
	private HorizontalBloomBlurShader horizontalBlurShader;
	private VerticalBloomBlurShader verticalBlurShader;
	private BloomSceneShader bloomSceneShader;
	private BloomAdditiveBlendShader additiveBlendShader;
	
	public Bloom(){
		
		sceneBrightnessShader = SceneBrightnessShader.getInstance();
		additiveBlendShader = BloomAdditiveBlendShader.getInstance();
		horizontalBlurShader = HorizontalBloomBlurShader.getInstance();
		verticalBlurShader = VerticalBloomBlurShader.getInstance();
		bloomSceneShader = BloomSceneShader.getInstance();
		
		sceneBrightnessTexture = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1);
		sceneBrightnessTexture.bind();
		sceneBrightnessTexture.clampToEdge();
		sceneBrightnessTexture.unbind();
		
		additiveBlendBloomTexture = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1);
		
		bloomSceneTexture = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth(),
						BaseContext.getWindow().getHeight(), 1);
		
		horizontalBloomBlurTexture_div2 = new Texture2DNoFilterRGBA16F(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2);
		horizontalBloomBlurTexture_div2.bind();
		horizontalBloomBlurTexture_div2.clampToEdge();
		horizontalBloomBlurTexture_div2.unbind();
		
		verticalBloomBlurTexture_div2 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2, 1);
		verticalBloomBlurTexture_div2.bind();
		verticalBloomBlurTexture_div2.clampToEdge();
		verticalBloomBlurTexture_div2.unbind();
		
		horizontalBloomBlurTexture_div4 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/4,
				BaseContext.getWindow().getHeight()/4, 1);
		horizontalBloomBlurTexture_div4.bind();
		horizontalBloomBlurTexture_div4.clampToEdge();
		horizontalBloomBlurTexture_div4.unbind();
		
		verticalBloomBlurTexture_div4 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/4,
				BaseContext.getWindow().getHeight()/4, 1);
		verticalBloomBlurTexture_div4.bind();
		verticalBloomBlurTexture_div4.clampToEdge();
		verticalBloomBlurTexture_div4.unbind();
		
		horizontalBloomBlurTexture_div8 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/8,
				BaseContext.getWindow().getHeight()/8, 1);
		horizontalBloomBlurTexture_div8.bind();
		horizontalBloomBlurTexture_div8.clampToEdge();
		horizontalBloomBlurTexture_div8.unbind();
		
		verticalBloomBlurTexture_div8 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/8,
				BaseContext.getWindow().getHeight()/8, 1);
		verticalBloomBlurTexture_div8.bind();
		verticalBloomBlurTexture_div8.clampToEdge();
		verticalBloomBlurTexture_div8.unbind();
		
		horizontalBloomBlurTexture_div12 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/12,
				BaseContext.getWindow().getHeight()/12, 1);
		horizontalBloomBlurTexture_div12.bind();
		horizontalBloomBlurTexture_div12.clampToEdge();
		horizontalBloomBlurTexture_div12.unbind();
		
		verticalBloomBlurTexture_div12 = new Texture2DStorageRGBA16F(BaseContext.getWindow().getWidth()/12,
				BaseContext.getWindow().getHeight()/12, 1);
		verticalBloomBlurTexture_div12.bind();
		verticalBloomBlurTexture_div12.clampToEdge();
		verticalBloomBlurTexture_div12.unbind();
	}
	
	public void render(GLTexture sceneSampler) {
				
		sceneBrightnessShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneBrightnessTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, BaseContext.getWindow().getWidth()/2, BaseContext.getWindow().getHeight()/2);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurTexture_div2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(horizontalBloomBlurTexture_div2, BaseContext.getWindow().getWidth()/2, BaseContext.getWindow().getHeight()/2);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div4.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, BaseContext.getWindow().getWidth()/4, BaseContext.getWindow().getHeight()/4);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurTexture_div4.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(horizontalBloomBlurTexture_div4, BaseContext.getWindow().getWidth()/4, BaseContext.getWindow().getHeight()/4);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div8.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurTexture_div8.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(horizontalBloomBlurTexture_div8, BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurTexture_div12.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, BaseContext.getWindow().getWidth()/12, BaseContext.getWindow().getHeight()/12);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurTexture_div12.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(horizontalBloomBlurTexture_div12, BaseContext.getWindow().getWidth()/12, BaseContext.getWindow().getHeight()/12);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		additiveBlendShader.bind();
		glBindImageTexture(0, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		additiveBlendShader.updateUniforms(verticalBloomBlurTexture_div2, verticalBloomBlurTexture_div4, verticalBloomBlurTexture_div8,
				verticalBloomBlurTexture_div12, BaseContext.getWindow().getWidth(), BaseContext.getWindow().getHeight());
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		bloomSceneShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, bloomSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}
	
}
