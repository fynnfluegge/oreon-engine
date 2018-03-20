package org.oreon.core.gl.context;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.texture.Texture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLConfiguration {
	
	public Texture sceneDepthMap;
	public GLFramebuffer deferredFbo;

	public Texture underwaterDudvMap;
	public Texture underwaterCausticsMap;
	public float underwaterDistortion;
	
}
