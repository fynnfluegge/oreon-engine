package org.oreon.core.gl.context;

import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLResources {
	
	private GLFrameBufferObject offScreenFbo;
	private GLTexture sceneDepthMap;
	private GLFramebuffer deferredFbo;

	private GLTexture underwaterDudvMap;
	private GLTexture underwaterCausticsMap;
	private float underwaterDistortion;
}
