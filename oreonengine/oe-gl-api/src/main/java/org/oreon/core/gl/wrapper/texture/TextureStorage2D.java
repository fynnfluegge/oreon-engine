package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import org.oreon.core.gl.texture.GLTexture;

public class TextureStorage2D extends GLTexture{

	public TextureStorage2D(int width, int height, ImageFormat imageFormat) {
		
		super(GL_TEXTURE_2D, width, height);
	}

}
