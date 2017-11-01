package org.oreon.core.gl.deferred;

import org.oreon.core.gl.config.TransparencyBlending;
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
					   Texture transparencyLayer, Texture transparencyLayerDepthMap){
		
		getConfig().enable();
		shader.bind();
		shader.updateUniforms(getOrthographicMatrix());
		shader.updateUniforms(opaqueScene, opaqueSceneDepthMap, 
								   transparencyLayer, transparencyLayerDepthMap);
		getVao().draw();
		getConfig().disable();
	}
}
