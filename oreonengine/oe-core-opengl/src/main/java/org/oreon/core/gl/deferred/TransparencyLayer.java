package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;

import java.nio.IntBuffer;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.util.BufferUtil;

public class TransparencyLayer {

	private GLFramebuffer fbo;
	private TransparencyGBuffer gbuffer;
	
	public TransparencyLayer(int width, int height) {
	
		gbuffer = new TransparencyGBuffer(width, height);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(3);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getId(),0);
		fbo.createColorTextureAttachment(gbuffer.getAlphaTexture().getId(),1);
		fbo.createColorTextureAttachment(gbuffer.getLightScatteringTexture().getId(),4);
		fbo.createDepthTextureAttachment(gbuffer.getDepthTexture().getId());
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
	}

	public TransparencyGBuffer getGbuffer() {
		return gbuffer;
	}

	public void setGbuffer(TransparencyGBuffer gbuffer) {
		this.gbuffer = gbuffer;
	}

	public GLFramebuffer getFbo() {
		return fbo;
	}

	public void setFbo(GLFramebuffer fbo) {
		this.fbo = fbo;
	}
}
