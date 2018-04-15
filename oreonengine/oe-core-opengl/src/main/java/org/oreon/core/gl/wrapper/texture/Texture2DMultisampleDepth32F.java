package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DMultisampleDepth32F extends GLTexture{

	public Texture2DMultisampleDepth32F(int width, int height, int samples) {
		super(GL_TEXTURE_2D_MULTISAMPLE, width, height);
		bind();
		allocateImage2DMultisample(samples, GL_DEPTH_COMPONENT32F);
		unbind();
	}

}
