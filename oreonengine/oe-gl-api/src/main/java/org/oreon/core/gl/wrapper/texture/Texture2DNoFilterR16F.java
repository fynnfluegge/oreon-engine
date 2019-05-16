package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_R16F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DNoFilterR16F extends GLTexture{

	public Texture2DNoFilterR16F(int width, int height) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateImage2D(GL_R16F, GL_RED, GL_FLOAT);
		nearestFilter();
		unbind();
	} 

}
