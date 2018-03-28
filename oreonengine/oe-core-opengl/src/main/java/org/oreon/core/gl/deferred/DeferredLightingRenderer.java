package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.shaders.DeferredLightingShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DArray;
import org.oreon.core.util.BufferUtil;

public class DeferredLightingRenderer {

	private GLFramebuffer fbo;
	private GBufferMultisample gbuffer;
	private DeferredLightingShader shader;
	private Texture2D deferredLightingSceneTexture;
	
	public DeferredLightingRenderer(int width, int height) {
		
		gbuffer = new GBufferMultisample(width, height);
		shader = DeferredLightingShader.getInstance();

		deferredLightingSceneTexture = new Texture2D();
		deferredLightingSceneTexture.generate();
		deferredLightingSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		deferredLightingSceneTexture.noFilter();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureMultisampleAttachment(gbuffer.getAlbedoTexture().getId(),0);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getWorldPositionTexture().getId(),1);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getNormalTexture().getId(),2);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getSpecularEmissionTexture().getId(),3);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getLightScatteringMask().getId(),4);
		fbo.createDepthTextureMultisampleAttachment(gbuffer.getDepthTexture().getId());
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
		
		GLContext.getRenderContext().setDeferredFbo(fbo);
	}
	
	public void render(Texture2D sampleCoverageMask, Texture2D ssaoBlurTexture, Texture2DArray pssm, boolean flag){
		
		shader.bind();
		glBindImageTexture(0, deferredLightingSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, gbuffer.getAlbedoTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, gbuffer.getWorldPositionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, gbuffer.getNormalTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(5, gbuffer.getSpecularEmissionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(6, sampleCoverageMask.getId(), 0, false, 0, GL_READ_ONLY, GL_R16F);
		glBindImageTexture(7, ssaoBlurTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms(pssm, flag);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16,1);
	}

	public GBufferMultisample getGbuffer() {
		return gbuffer;
	}
	public void setGbuffer(GBufferMultisample gbuffer) {
		this.gbuffer = gbuffer;
	}
	public Texture2D getDeferredLightingSceneTexture() {
		return deferredLightingSceneTexture;
	}
	public void setDeferredLightingSceneTexture(Texture2D texture) {
		this.deferredLightingSceneTexture = texture;
	}
	public GLFramebuffer getFbo() {
		return fbo;
	}
	public void setFbo(GLFramebuffer fbo) {
		this.fbo = fbo;
	}
}
