package org.oreon.gl.components.filter.dofblur;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA16F;

import lombok.Getter;

public class DepthOfFieldBlur {
	
	private GLTexture horizontalBlurSceneTexture;
	@Getter
	private GLTexture verticalBlurSceneTexture;
	private DepthOfFieldHorizontalBlurShader horizontalBlurShader;
	private DepthOfFieldVerticalBlurShader verticalBlurShader;
	
	private GLFramebuffer lowResFbo;
	private FullScreenQuad fullScreenQuad;
	private GLTexture lowResSceneSampler;
		
	public DepthOfFieldBlur() {
		
		horizontalBlurShader = DepthOfFieldHorizontalBlurShader.getInstance();
		verticalBlurShader = DepthOfFieldVerticalBlurShader.getInstance();
		
		fullScreenQuad = new FullScreenQuad();
		
		horizontalBlurSceneTexture = new Texture2DStorageRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight(),1);
		
		verticalBlurSceneTexture = new Texture2DStorageRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight(),1);
		
		lowResSceneSampler = new Texture2DBilinearFilterRGBA16F((int)(EngineContext.getWindow().getWidth()/1.2f),
				(int)(EngineContext.getWindow().getHeight()/1.2f));
		lowResSceneSampler.bind();
		lowResSceneSampler.clampToEdge();
		lowResSceneSampler.unbind();
		
		lowResFbo = new GLFramebuffer();
		lowResFbo.bind();
		lowResFbo.createColorTextureAttachment(lowResSceneSampler.getHandle(), 0);
		lowResFbo.checkStatus();
		lowResFbo.unbind();
	}
	
	public void render(GLTexture depthmap, GLTexture lightScatteringMask, GLTexture sceneSampler, int width, int height) {
		
		getLowResFbo().bind();
		fullScreenQuad.setTexture(sceneSampler);
		glViewport(0,0,(int)(width/1.2f),(int)(height/1.2f));
		fullScreenQuad.render();
		getLowResFbo().unbind();
		glViewport(0,0, width, height);
		
		horizontalBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, lowResSceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, lightScatteringMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		horizontalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		verticalBlurShader.bind();
		glBindImageTexture(0, horizontalBlurSceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, lightScatteringMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, verticalBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		verticalBlurShader.updateUniforms(depthmap);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}

	public GLFramebuffer getLowResFbo() {
		return lowResFbo;
	}
}
