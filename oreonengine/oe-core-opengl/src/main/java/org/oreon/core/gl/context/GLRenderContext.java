package org.oreon.core.gl.context;

import org.oreon.core.gl.buffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLRenderContext {
	
	private GLTexture sceneDepthMap;
	private GLFramebuffer deferredFbo;

	private GLTexture underwaterDudvMap;
	private GLTexture underwaterCausticsMap;
	private float underwaterDistortion;
}
