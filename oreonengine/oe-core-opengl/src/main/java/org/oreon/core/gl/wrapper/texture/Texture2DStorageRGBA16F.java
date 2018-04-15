package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DStorageRGBA16F extends GLTexture{

	public Texture2DStorageRGBA16F(int width, int height, int levels) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateStorage2D(levels, GL_RGBA16F);
		unbind();
	}

}
