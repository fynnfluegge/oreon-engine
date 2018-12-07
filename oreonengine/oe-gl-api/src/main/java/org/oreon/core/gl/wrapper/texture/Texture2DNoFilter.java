package org.oreon.core.gl.wrapper.texture;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DNoFilter extends GLTexture{

	public Texture2DNoFilter(String file) {
		
		super(file);
		
		bind();
		noFilter();
		unbind();
	}

}
