package org.oreon.gl.components.filter.bloom;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
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
	
	private final int[] downsamplingFactors = {2,4,8,16};
	
	public Bloom(){
		
		sceneBrightnessShader = SceneBrightnessShader.getInstance();
		additiveBlendShader = BloomAdditiveBlendShader.getInstance();
		horizontalBlurShader = BloomHorizontalBlurShader.getInstance();
		verticalBlurShader = BloomVerticalBlurShader.getInstance();
		bloomSceneShader = BloomSceneBlendingShader.getInstance();
		
		sceneBrightnessTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge); 
		
		additiveBlendBloomTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
		
		bloomSceneTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling0 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[0],
				BaseContext.getWindow().getHeight()/downsamplingFactors[0], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling0 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[0],
				BaseContext.getWindow().getHeight()/downsamplingFactors[0], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling1 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[1],
				BaseContext.getWindow().getHeight()/downsamplingFactors[1], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling1 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[1],
				BaseContext.getWindow().getHeight()/downsamplingFactors[1], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling2 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[2],
				BaseContext.getWindow().getHeight()/downsamplingFactors[2], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling2 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[2],
				BaseContext.getWindow().getHeight()/downsamplingFactors[2], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		horizontalBloomBlurDownsampling3 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[3],
				BaseContext.getWindow().getHeight()/downsamplingFactors[3], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		
		verticalBloomBlurDownsampling3 = new TextureImage2D(BaseContext.getWindow().getWidth()/downsamplingFactors[3],
				BaseContext.getWindow().getHeight()/downsamplingFactors[3], ImageFormat.RGBA16FLOAT,
				SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
	}
	
	public void render(GLTexture sceneSamplerPrePostprocessing, GLTexture sceneSampler, GLTexture specular_emission_bloom_attachment) {
		
		sceneBrightnessShader.bind();
		glBindImageTexture(0, sceneSamplerPrePostprocessing.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneBrightnessTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, horizontalBloomBlurDownsampling0.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(sceneBrightnessTexture, downsamplingFactors,
				BaseContext.getWindow().getWidth()/downsamplingFactors[0], BaseContext.getWindow().getHeight()/downsamplingFactors[0]);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		glBindImageTexture(0, horizontalBloomBlurDownsampling1.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(horizontalBloomBlurDownsampling0, downsamplingFactors,
				BaseContext.getWindow().getWidth()/downsamplingFactors[1], BaseContext.getWindow().getHeight()/downsamplingFactors[1]);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		glBindImageTexture(0, horizontalBloomBlurDownsampling2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(horizontalBloomBlurDownsampling1, downsamplingFactors,
				BaseContext.getWindow().getWidth()/downsamplingFactors[2], BaseContext.getWindow().getHeight()/downsamplingFactors[2]);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		glBindImageTexture(0, horizontalBloomBlurDownsampling3.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(horizontalBloomBlurDownsampling2, downsamplingFactors,
				BaseContext.getWindow().getWidth()/downsamplingFactors[3], BaseContext.getWindow().getHeight()/downsamplingFactors[3]);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, verticalBloomBlurDownsampling0.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, verticalBloomBlurDownsampling1.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, verticalBloomBlurDownsampling2.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(3, verticalBloomBlurDownsampling3.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(4, horizontalBloomBlurDownsampling0.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(5, horizontalBloomBlurDownsampling1.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(6, horizontalBloomBlurDownsampling2.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(7, horizontalBloomBlurDownsampling3.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
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
