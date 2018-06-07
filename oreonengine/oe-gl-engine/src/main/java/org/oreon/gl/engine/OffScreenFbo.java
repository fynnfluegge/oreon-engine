package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;

import java.nio.IntBuffer;

import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleDepth32F;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleRGBA32F;
import org.oreon.core.util.BufferUtil;

public class OffScreenFbo extends GLFrameBufferObject{

	public OffScreenFbo(int width, int height, int samples) {
		
		GLTexture albedoTexture = new Texture2DMultisampleRGBA16F(width, height, samples);
		GLTexture worldPositionTexture = new Texture2DMultisampleRGBA32F(width, height, samples);
		GLTexture normalTexture = new Texture2DMultisampleRGBA32F(width, height, samples);
		GLTexture specularEmissionTexture = new Texture2DMultisampleRGBA16F(width, height, samples);
		GLTexture lightScatteringMask = new Texture2DMultisampleRGBA16F(width, height, samples);
		GLTexture depthTexture = new Texture2DMultisampleDepth32F(width, height, samples);

		attachments.put(Attachment.ALBEDO, albedoTexture);
		attachments.put(Attachment.POSITION, worldPositionTexture);
		attachments.put(Attachment.NORMAL, normalTexture);
		attachments.put(Attachment.SPECULAR_EMISSION, specularEmissionTexture);
		attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringMask);
		attachments.put(Attachment.DEPTH, depthTexture);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		frameBuffer = new GLFramebuffer();
		frameBuffer.bind();
		frameBuffer.createColorTextureMultisampleAttachment(albedoTexture.getHandle(),0);
		frameBuffer.createColorTextureMultisampleAttachment(worldPositionTexture.getHandle(),1);
		frameBuffer.createColorTextureMultisampleAttachment(normalTexture.getHandle(),2);
		frameBuffer.createColorTextureMultisampleAttachment(specularEmissionTexture.getHandle(),3);
		frameBuffer.createColorTextureMultisampleAttachment(lightScatteringMask.getHandle(),4);
		frameBuffer.createDepthTextureMultisampleAttachment(depthTexture.getHandle());
		frameBuffer.setDrawBuffers(drawBuffers);
		frameBuffer.checkStatus();
		frameBuffer.unbind();
	}
}
