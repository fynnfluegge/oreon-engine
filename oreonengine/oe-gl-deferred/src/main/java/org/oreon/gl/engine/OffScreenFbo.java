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
		
		GLTexture albedoTexture = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture worldPositionTexture = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture normalTexture = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture specularEmissionTexture = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture lightScatteringMask = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);
		GLTexture depthTexture = new TextureImage2DMultisample(width, height, samples,
				ImageFormat.DEPTH32FLOAT, SamplerFilter.Nearest, TextureWrapMode.ClampToEdge);

		attachments.put(Attachment.COLOR, albedoTexture);
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
