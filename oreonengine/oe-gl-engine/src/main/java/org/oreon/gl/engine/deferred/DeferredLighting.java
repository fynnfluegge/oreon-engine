package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.IntBuffer;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.util.BufferUtil;

import lombok.Getter;

public class DeferredLighting {

	@Getter
	private GLTexture deferredLightingSceneTexture;
	private GLFramebuffer fbo;
	private GBufferMultisample gbuffer;
	private DeferredLightingShader shader;
	
	public DeferredLighting(int width, int height) {
		
		gbuffer = new GBufferMultisample(width, height, EngineContext.getConfig().getMultisamples());
		shader = DeferredLightingShader.getInstance();

		deferredLightingSceneTexture = new Texture2DNoFilterRGBA16F(width, height);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureMultisampleAttachment(gbuffer.getAlbedoTexture().getHandle(),0);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getWorldPositionTexture().getHandle(),1);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getNormalTexture().getHandle(),2);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getSpecularEmissionTexture().getHandle(),3);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getLightScatteringMask().getHandle(),4);
		fbo.createDepthTextureMultisampleAttachment(gbuffer.getDepthTexture().getHandle());
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
		
		GLContext.getResources().setDeferredFbo(fbo);
	}
	
	public void render(GLTexture sampleCoverageMask, GLTexture ssaoBlurTexture, GLTexture pssm,
			boolean ssaoFlag){
		
		shader.bind();
		glBindImageTexture(0, deferredLightingSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, gbuffer.getAlbedoTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, gbuffer.getWorldPositionTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, gbuffer.getNormalTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(5, gbuffer.getSpecularEmissionTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(6, sampleCoverageMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_R16F);
		glBindImageTexture(7, ssaoBlurTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms(pssm, ssaoFlag);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16,1);
	}

	public GBufferMultisample getGbuffer() {
		return gbuffer;
	}
	public void setGbuffer(GBufferMultisample gbuffer) {
		this.gbuffer = gbuffer;
	}
	public GLFramebuffer getFbo() {
		return fbo;
	}
	public void setFbo(GLFramebuffer fbo) {
		this.fbo = fbo;
	}
}
