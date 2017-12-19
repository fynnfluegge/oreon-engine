package org.oreon.core.gl.deferred;

import org.oreon.core.gl.scene.FullScreenQuad;
import org.oreon.core.gl.shaders.TransparencyBlendShader;
import org.oreon.core.texture.Texture;

public class TransparencyBlendRenderer extends FullScreenQuad{
	
	private TransparencyBlendShader shader;
	
	public TransparencyBlendRenderer() {
		
		super();
		
		shader = TransparencyBlendShader.getInstance();
	}
	
	public void render(Texture opaqueScene, Texture opaqueSceneDepthMap,
					   Texture opaqueSceneLightScatteringTexture,
					   Texture transparencyLayer, Texture transparencyLayerDepthMap,
					   Texture alphaMap, Texture transparencyLayerLightScatteringTexture){
		
		getConfig().enable();
		shader.bind();
		shader.updateUniforms(opaqueScene, opaqueSceneDepthMap, opaqueSceneLightScatteringTexture, 
							  transparencyLayer, transparencyLayerDepthMap,
							  alphaMap, transparencyLayerLightScatteringTexture);
		getVao().draw();
		getConfig().disable();
	}
}
