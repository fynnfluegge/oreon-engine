package org.oreon.core.gl.deferred;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleDepth32F;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DMultisampleRGBA32F;

import lombok.Getter;

@Getter
public class GBufferMultisample {

	private GLTexture albedoTexture;
	private GLTexture normalTexture;
	private GLTexture worldPositionTexture;
	private GLTexture specularEmissionTexture;
	private GLTexture lightScatteringMask;
	private GLTexture depthTexture;
	
	public GBufferMultisample(int width, int height, int samples) {
		
		albedoTexture = new Texture2DMultisampleRGBA16F(width, height, samples);
		worldPositionTexture = new Texture2DMultisampleRGBA32F(width, height, samples);
		normalTexture = new Texture2DMultisampleRGBA32F(width, height, samples);
		specularEmissionTexture = new Texture2DMultisampleRGBA16F(width, height, samples);
		lightScatteringMask = new Texture2DMultisampleRGBA16F(width, height, samples);
		depthTexture = new Texture2DMultisampleDepth32F(width, height, samples);
	}
	
}
