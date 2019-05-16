package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DNoFilterRGBA16F extends GLTexture{

	public Texture2DNoFilterRGBA16F(int width, int height) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateImage2D(GL_RGBA16F, GL_RGBA, GL_FLOAT);
		nearestFilter();
		clampToEdge();
		unbind();
	}

}
