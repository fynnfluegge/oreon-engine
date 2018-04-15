package org.oreon.gl.components.water;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA32F;

import lombok.Getter;

@Getter
public class RefracReflecGBuffer {
	
	private GLTexture albedoTexture;
	private GLTexture normalTexture;
	
	public RefracReflecGBuffer(int width, int height) {
		
		albedoTexture = new Texture2DNoFilterRGBA16F(width, height);
		normalTexture = new Texture2DNoFilterRGBA32F(width, height);
	}

}
