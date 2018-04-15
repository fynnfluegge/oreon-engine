package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DNoFilterRGBA32F extends GLTexture{

	public Texture2DNoFilterRGBA32F(int width, int height) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateImage2D(GL_RGBA32F, GL_RGBA, GL_FLOAT);
		noFilter();
		unbind();
	}

}
