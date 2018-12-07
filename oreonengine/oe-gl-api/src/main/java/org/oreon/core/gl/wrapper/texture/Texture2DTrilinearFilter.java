package org.oreon.core.gl.wrapper.texture;

import org.oreon.core.gl.texture.GLTexture;

public class Texture2DTrilinearFilter extends GLTexture{

	public Texture2DTrilinearFilter(String file) {
		
		super(file);
		
		bind();
		trilinearFilter();
		unbind();
	}

}
