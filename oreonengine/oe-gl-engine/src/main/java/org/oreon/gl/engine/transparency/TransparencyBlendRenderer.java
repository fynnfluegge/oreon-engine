package org.oreon.gl.engine.transparency;

import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;

public class TransparencyBlendRenderer extends FullScreenQuad{
	
	private TransparencyBlendShader shader;
	
	public TransparencyBlendRenderer() {
		
		super();
		
		shader = TransparencyBlendShader.getInstance();
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
