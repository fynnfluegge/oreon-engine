package org.oreon.gl.engine.transparency;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;

import lombok.Getter;

public class OpaqueTransparencyBlending extends FullScreenQuad{
	
	private OpaqueTransparencyBlendShader shader;
	@Getter
	private GLTexture blendedSceneTexture;
	@Getter
	private GLTexture blendedLightScatteringTexture;
	
	public OpaqueTransparencyBlending(int width, int height) {
		
		super();
		shader = OpaqueTransparencyBlendShader.getInstance();
		blendedSceneTexture = new Texture2DBilinearFilterRGBA16F(width, height);
		blendedLightScatteringTexture = new Texture2DBilinearFilterRGBA16F(width, height);
	}
	
	public void render(GLTexture opaqueScene, GLTexture opaqueSceneDepthMap,
			GLTexture opaqueSceneLightScatteringTexture,
			GLTexture transparencyLayer, GLTexture transparencyLayerDepthMap,
			GLTexture alphaMap, GLTexture transparencyLayerLightScatteringTexture){
		
		shader.bind();
		glBindImageTexture(0, blendedSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, blendedLightScatteringTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, opaqueSceneDepthMap.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, transparencyLayerDepthMap.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms(opaqueScene, opaqueSceneDepthMap, opaqueSceneLightScatteringTexture, 
							  transparencyLayer, transparencyLayerDepthMap,
							  alphaMap, transparencyLayerLightScatteringTexture);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16, 1);
	}

}
