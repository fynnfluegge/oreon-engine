package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureStorage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.TextureWrapMode;

import lombok.Getter;

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
	private BloomHorizontalBlurShader horizontalBlurShader;
	private BloomVerticalBlurShader verticalBlurShader;
	private BloomSceneBlendingShader bloomSceneShader;
	private BloomAdditiveBlendShader additiveBlendShader;
	
	public Bloom(){
		
		sceneBrightnessShader = SceneBrightnessShader.getInstance();
		additiveBlendShader = BloomAdditiveBlendShader.getInstance();
		horizontalBlurShader = BloomHorizontalBlurShader.getInstance();
		verticalBlurShader = BloomVerticalBlurShader.getInstance();
		bloomSceneShader = BloomSceneBlendingShader.getInstance();
		
		sceneBrightnessTexture = new TextureStorage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge); 
		
		additiveBlendBloomTexture = new TextureStorage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		bloomSceneTexture = new TextureStorage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurTexture_div2 = new TextureStorage2D(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurTexture_div2 = new TextureStorage2D(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurTexture_div4 = new TextureStorage2D(BaseContext.getWindow().getWidth()/4,
				BaseContext.getWindow().getHeight()/4, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurTexture_div4 = new TextureStorage2D(BaseContext.getWindow().getWidth()/4,
				BaseContext.getWindow().getHeight()/4, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurTexture_div8 = new TextureStorage2D(BaseContext.getWindow().getWidth()/8,
				BaseContext.getWindow().getHeight()/8, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurTexture_div8 = new TextureStorage2D(BaseContext.getWindow().getWidth()/8,
				BaseContext.getWindow().getHeight()/8, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurTexture_div12 = new TextureStorage2D(BaseContext.getWindow().getWidth()/12,
				BaseContext.getWindow().getHeight()/12, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurTexture_div12 = new TextureStorage2D(BaseContext.getWindow().getWidth()/12,
				BaseContext.getWindow().getHeight()/12, 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
	}
	
	public void render(GLTexture sceneSamplerPrePostprocessing, GLTexture sceneSampler) {
				
		sceneBrightnessShader.bind();
		glBindImageTexture(0, sceneSamplerPrePostprocessing.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
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
