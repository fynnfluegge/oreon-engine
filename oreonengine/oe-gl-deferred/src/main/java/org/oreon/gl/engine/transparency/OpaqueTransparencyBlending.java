package org.oreon.gl.engine.transparency;

import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

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
		blendedSceneTexture = new TextureImage2D(width, height,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.None);
		blendedLightScatteringTexture = new TextureImage2D(width, height,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.None);
	}
	
	public void render(GLTexture opaqueScene, GLTexture opaqueSceneDepthMap,
			GLTexture opaqueSceneLightScatteringTexture,
			GLTexture transparencyLayer, GLTexture transparencyLayerDepthMap,
			GLTexture alphaMap, GLTexture transparencyLayerLightScatteringTexture){
		
		shader.bind();
		glBindImageTexture(0, blendedSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, blendedLightScatteringTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		shader.updateUniforms(opaqueScene, opaqueSceneLightScatteringTexture, 
							  transparencyLayer, alphaMap, transparencyLayerLightScatteringTexture,
							  opaqueSceneDepthMap, transparencyLayerDepthMap);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);
	}

}
