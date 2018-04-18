package org.oreon.gl.engine.transparency;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;

import java.nio.IntBuffer;

import org.oreon.core.gl.buffer.GLFramebuffer;
import org.oreon.core.util.BufferUtil;

public class TransparencyLayer {

	private GLFramebuffer fbo;
	private TransparencyGBuffer gbuffer;
	
	public TransparencyLayer(int width, int height) {
	
		gbuffer = new TransparencyGBuffer(width, height);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getHandle(),0);
		fbo.createColorTextureAttachment(gbuffer.getAlphaTexture().getHandle(),1);
		fbo.createColorTextureAttachment(gbuffer.getLightScatteringMask().getHandle(),4);
		fbo.createDepthTextureAttachment(gbuffer.getDepthTexture().getHandle());
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
