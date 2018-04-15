package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DStorageRGBA32F extends GLTexture{

	public Texture2DStorageRGBA32F(int width, int height, int levels) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateStorage2D(levels, GL_RGBA32F);
		unbind();
	}

}
