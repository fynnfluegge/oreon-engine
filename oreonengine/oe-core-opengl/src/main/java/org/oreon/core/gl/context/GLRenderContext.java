package org.oreon.core.gl.context;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.texture.Texture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLRenderContext {
	
	private Texture sceneDepthMap;
	private GLFramebuffer deferredFbo;

	private Texture underwaterDudvMap;
	private Texture underwaterCausticsMap;
	private float underwaterDistortion;
	
	private GLCamera camera;
}
