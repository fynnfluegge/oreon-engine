package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DArrayDepth32F extends GLTexture{
	
	public Texture2DArrayDepth32F(int width, int height, int layers) {
		super(GL_TEXTURE_2D_ARRAY, width, height);
		
		bind();
		allocateStorage3D(1, layers, GL_DEPTH_COMPONENT32F);
		bilinearFilter();
		clampToEdge();
		unbind();
	}

}
