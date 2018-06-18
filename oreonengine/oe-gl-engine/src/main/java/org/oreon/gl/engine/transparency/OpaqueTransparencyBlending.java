package org.oreon.gl.engine.transparency;

import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;

public class OpaqueTransparencyBlending extends FullScreenQuad{
	
	private GLFrameBufferObject fbo;
	private OpaqueTransparencyBlendingShader shader;
	
	public OpaqueTransparencyBlending() {
		
		super();
		
		shader = OpaqueTransparencyBlendingShader.getInstance();
	}
	
	public void render(GLTexture opaqueScene, GLTexture opaqueSceneDepthMap,
			GLTexture opaqueSceneLightScatteringTexture,
			GLTexture transparencyLayer, GLTexture transparencyLayerDepthMap,
			GLTexture alphaMap, GLTexture transparencyLayerLightScatteringTexture){
		
		getConfig().enable();
		shader.bind();
		shader.updateUniforms(opaqueScene, opaqueSceneDepthMap, opaqueSceneLightScatteringTexture, 
							  transparencyLayer, transparencyLayerDepthMap,
							  alphaMap, transparencyLayerLightScatteringTexture);
		getVao().draw();
		getConfig().disable();
	}
}
