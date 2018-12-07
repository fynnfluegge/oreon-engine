package org.oreon.core.gl.wrapper.texture;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DBilinearFilter extends GLTexture{

	public Texture2DBilinearFilter(String file) {
		
		super(file);
		
		bind();
		bilinearFilter();
		unbind();
	}

}
