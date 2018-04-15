package org.oreon.core.gl.deferred;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterDepth32F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;

import lombok.Getter;

@Getter
public class TransparencyGBuffer {

	private GLTexture albedoTexture;
	private GLTexture alphaTexture;
	private GLTexture lightScatteringMask;
	private GLTexture depthTexture;
	
	public TransparencyGBuffer(int width, int height) {
	
		albedoTexture = new Texture2DNoFilterRGBA16F(width, height);
		alphaTexture = new Texture2DNoFilterRGBA16F(width, height);
		lightScatteringMask = new Texture2DNoFilterRGBA16F(width, height);
		depthTexture = new Texture2DNoFilterDepth32F(width, height);
	}

}
