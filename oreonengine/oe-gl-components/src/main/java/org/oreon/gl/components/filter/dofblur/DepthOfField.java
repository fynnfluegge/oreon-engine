package org.oreon.gl.components.filter.dofblur;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.gl.wrapper.texture.TextureStorage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

import lombok.Getter;

public class DepthOfField {
	
	private GLTexture horizontalBlurSceneTexture;
	@Getter
	private GLTexture verticalBlurSceneTexture;
	private DepthOfFieldHorizontalBlurShader horizontalBlurShader;
	private DepthOfFieldVerticalBlurShader verticalBlurShader;
	
	private GLFramebuffer lowResFbo;
	private FullScreenQuad fullScreenQuad;
	private GLTexture lowResSceneSampler;
		
	public DepthOfField() {
		
		horizontalBlurShader = DepthOfFieldHorizontalBlurShader.getInstance();
		verticalBlurShader = DepthOfFieldVerticalBlurShader.getInstance();
		
		fullScreenQuad = new FullScreenQuad();
		
		horizontalBlurSceneTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
		
		verticalBlurSceneTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge); 
				
				new TextureStorage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(), 1, ImageFormat.RGBA16FLOAT);
		
		lowResSceneSampler = new TextureImage2D((int)(BaseContext.getWindow().getWidth()/1.2f),
				(int)(BaseContext.getWindow().getHeight()/1.2f), ImageFormat.RGBA16FLOAT,
				SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
		
		lowResFbo = new GLFramebuffer();
		lowResFbo.bind();
		lowResFbo.createColorTextureAttachment(lowResSceneSampler.getHandle(), 0);
		lowResFbo.checkStatus();
		lowResFbo.unbind();
	}
	
	public void render(GLTexture depthmap, GLTexture lightScatteringMask, GLTexture sceneSampler) {
		
		lowResFbo.bind();
		fullScreenQuad.setTexture(sceneSampler);
		glViewport(0,0,(int)(BaseContext.getConfig().getX_ScreenResolution()/1.2f),
				(int)(BaseContext.getConfig().getY_ScreenResolution()/1.2f));
		fullScreenQuad.render();
		lowResFbo.unbind();
		glViewport(0,0, BaseContext.getConfig().getX_ScreenResolution(), BaseContext.getConfig().getY_ScreenResolution());
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, lowResSceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, lightScatteringMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, lightScatteringMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, verticalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}

}
