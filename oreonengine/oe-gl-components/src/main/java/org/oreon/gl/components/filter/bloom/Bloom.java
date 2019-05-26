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

@Getter
public class Bloom {

	@Getter
	private GLTexture bloomSceneTexture;
	
	private GLTexture sceneBrightnessTexture;
	private GLTexture horizontalBloomBlurDownsampling0;
	private GLTexture verticalBloomBlurDownsampling0;
	private GLTexture horizontalBloomBlurDownsampling1;
	private GLTexture verticalBloomBlurDownsampling1;
	private GLTexture horizontalBloomBlurDownsampling2;
	private GLTexture verticalBloomBlurDownsampling2;
	private GLTexture horizontalBloomBlurDownsampling3;
	private GLTexture verticalBloomBlurDownsampling3;
	private GLTexture additiveBlendBloomTexture;
	
	private SceneBrightnessShader sceneBrightnessShader;
	private BloomHorizontalBlurShader horizontalBlurShader;
	private BloomVerticalBlurShader verticalBlurShader;
	private BloomSceneBlendingShader bloomSceneShader;
	private BloomAdditiveBlendShader additiveBlendShader;
	
	private final int[] downsamplingFactors = {4,8,12,16};
	
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
		
		horizontalBloomBlurDownsampling0 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[0],
				BaseContext.getWindow().getHeight()/downsamplingFactors[0], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling0 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[0],
				BaseContext.getWindow().getHeight()/downsamplingFactors[0], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling1 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[1],
				BaseContext.getWindow().getHeight()/downsamplingFactors[1], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling1 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[1],
				BaseContext.getWindow().getHeight()/downsamplingFactors[1], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling2 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[2],
				BaseContext.getWindow().getHeight()/downsamplingFactors[2], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling2 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[2],
				BaseContext.getWindow().getHeight()/downsamplingFactors[2], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling3 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[3],
				BaseContext.getWindow().getHeight()/downsamplingFactors[3], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling3 = new TextureStorage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[3],
				BaseContext.getWindow().getHeight()/downsamplingFactors[3], 1, ImageFormat.RGBA16FLOAT, TextureWrapMode.ClampToEdge);
	}
	
	public void render(GLTexture sceneSamplerPrePostprocessing, GLTexture sceneSampler, GLTexture specular_emission_bloom_attachment) {
		
		sceneBrightnessShader.bind();
		glBindImageTexture(0, sceneSamplerPrePostprocessing.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneBrightnessTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurDownsampling0.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, horizontalBloomBlurDownsampling1.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, horizontalBloomBlurDownsampling2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(3, horizontalBloomBlurDownsampling3.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, downsamplingFactors,
				BaseContext.getWindow().getWidth(), BaseContext.getWindow().getHeight());
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurDownsampling0.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurDownsampling1.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, verticalBloomBlurDownsampling2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(3, verticalBloomBlurDownsampling3.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(horizontalBloomBlurDownsampling0, horizontalBloomBlurDownsampling1,
				horizontalBloomBlurDownsampling2, horizontalBloomBlurDownsampling3, downsamplingFactors,
				BaseContext.getWindow().getWidth(), BaseContext.getWindow().getHeight());
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		additiveBlendShader.bind();
		glBindImageTexture(0, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		additiveBlendShader.updateUniforms(verticalBloomBlurDownsampling0, verticalBloomBlurDownsampling1, verticalBloomBlurDownsampling2,
				verticalBloomBlurDownsampling3, BaseContext.getWindow().getWidth(), BaseContext.getWindow().getHeight());
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		bloomSceneShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, additiveBlendBloomTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, specular_emission_bloom_attachment.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, bloomSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}
	
}
