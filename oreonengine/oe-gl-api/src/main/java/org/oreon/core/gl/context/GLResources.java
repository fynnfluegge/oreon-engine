package org.oreon.core.gl.context;

import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.texture.GLTexture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLResources {
	
	private GLFrameBufferObject primaryFbo;
	private GLTexture sceneDepthMap;
	
	private GLTexture underwaterDudvMap;
	private GLTexture underwaterCausticsMap;
	
	private WaterConfiguration waterConfig;
}
