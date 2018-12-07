package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;

import java.nio.IntBuffer;

import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterDepth32F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.util.BufferUtil;

public class TransparencyFbo extends GLFrameBufferObject{

	public TransparencyFbo(int width, int height) {

		GLTexture albedoTexture = new Texture2DNoFilterRGBA16F(width, height);
		GLTexture alphaTexture = new Texture2DNoFilterRGBA16F(width, height);
		GLTexture lightScatteringMask = new Texture2DNoFilterRGBA16F(width, height);
		GLTexture depthTexture = new Texture2DNoFilterDepth32F(width, height);
		
		attachments.put(Attachment.ALBEDO, albedoTexture);
		attachments.put(Attachment.ALPHA, alphaTexture);
		attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringMask);
		attachments.put(Attachment.DEPTH, depthTexture);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(3);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.flip();
		
		frameBuffer = new GLFramebuffer();
		frameBuffer.bind();
		frameBuffer.createColorTextureAttachment(albedoTexture.getHandle(),0);
		frameBuffer.createColorTextureAttachment(alphaTexture.getHandle(),1);
		frameBuffer.createColorTextureAttachment(lightScatteringMask.getHandle(),2);
		frameBuffer.createDepthTextureAttachment(depthTexture.getHandle());
		frameBuffer.setDrawBuffers(drawBuffers);
		frameBuffer.checkStatus();
		frameBuffer.unbind();
	}
}
