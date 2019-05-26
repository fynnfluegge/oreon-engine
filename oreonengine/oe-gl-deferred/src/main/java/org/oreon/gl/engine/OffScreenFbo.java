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
import org.oreon.core.gl.wrapper.texture.TextureImage2DMultisample;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;
import org.oreon.core.util.BufferUtil;

public class OffScreenFbo extends GLFrameBufferObject{

	public OffScreenFbo(int width, int height, int samples) {
		
		GLTexture albedoAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture worldPositionAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture normalAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture specularEmissionBloomAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture lightScatteringAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture depthAttachment = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.DEPTH32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);

		attachments.put(Attachment.COLOR, albedoAttachment);
		attachments.put(Attachment.POSITION, worldPositionAttachment);
		attachments.put(Attachment.NORMAL, normalAttachment);
		attachments.put(Attachment.SPECULAR_EMISSION_BLOOM, specularEmissionBloomAttachment);
		attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringAttachment);
		attachments.put(Attachment.DEPTH, depthAttachment);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		frameBuffer = new GLFramebuffer();
		frameBuffer.bind();
		frameBuffer.createColorTextureMultisampleAttachment(albedoAttachment.getHandle(),0);
		frameBuffer.createColorTextureMultisampleAttachment(worldPositionAttachment.getHandle(),1);
		frameBuffer.createColorTextureMultisampleAttachment(normalAttachment.getHandle(),2);
		frameBuffer.createColorTextureMultisampleAttachment(specularEmissionBloomAttachment.getHandle(),3);
		frameBuffer.createColorTextureMultisampleAttachment(lightScatteringAttachment.getHandle(),4);
		frameBuffer.createDepthTextureMultisampleAttachment(depthAttachment.getHandle());
		frameBuffer.setDrawBuffers(drawBuffers);
		frameBuffer.checkStatus();
		frameBuffer.unbind();
	}
}
