package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DNoFilterDepth32F extends GLTexture{

	public Texture2DNoFilterDepth32F(int width, int height) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		allocateImage2D(GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT, GL_FLOAT);
		noFilter();
		unbind();
	}
	
}
