package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.IntBuffer;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.util.BufferUtil;

public class TransparencyLayer {

	private GLFramebuffer mulisampleFbo;
	private GLFramebuffer fbo;
	private TransparencyGBuffer gbuffer;
	
	private int width;
	private int height;
	
	public TransparencyLayer(int width, int height) {
	
		this.width = width;
		this.height = height;
		
		gbuffer = new TransparencyGBuffer(width, height);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		mulisampleFbo = new GLFramebuffer();
		mulisampleFbo.bind();
		mulisampleFbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getId(),0);
		mulisampleFbo.createColorTextureAttachment(gbuffer.getAlphaTexture().getId(),1);
		mulisampleFbo.createDepthBufferAttachment(width,height);
		mulisampleFbo.setDrawBuffers(drawBuffers);
		mulisampleFbo.checkStatus();
		mulisampleFbo.unbind();
		
//		fbo = new GLFramebuffer();
//		fbo.bind();
//		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getId(),0);
//		fbo.createDepthTextureAttachment(gbuffer.getDepthTexture().getId());
//		fbo.setDrawBuffers(drawBuffers);
//		fbo.checkStatus();
//		fbo.unbind();
	}
	
	public void blitBuffers(){
		
		mulisampleFbo.blitFrameBuffer(0,0,fbo.getId(), width, height);

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

	public GLFramebuffer getMulisampleFbo() {
		return mulisampleFbo;
	}

	public void setMulisampleFbo(GLFramebuffer mulisampleFbo) {
		this.mulisampleFbo = mulisampleFbo;
	}
}
