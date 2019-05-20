package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

import lombok.Getter;

public class DeferredLighting {

	@Getter
	private GLTexture deferredLightingSceneTexture;
	private DeferredLightingShader shader;
	
	public DeferredLighting(int width, int height) {
		
		shader = DeferredLightingShader.getInstance();

		deferredLightingSceneTexture = new TextureImage2D(width, height,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.None);
	}
	
	public void render(GLTexture sampleCoverageMask, GLTexture ssaoBlurTexture, GLTexture pssm,
			GLTexture offScreenAlbedoTexture, GLTexture offScreenWorldPositionTexture,
			GLTexture offScreenNormalTexture, GLTexture offScreenSpecularEmissionTexture,
			boolean ssaoFlag){
		
		shader.bind();
		glBindImageTexture(0, deferredLightingSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(2, offScreenAlbedoTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, offScreenWorldPositionTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, offScreenNormalTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(5, offScreenSpecularEmissionTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(6, sampleCoverageMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_R16F);
		glBindImageTexture(7, ssaoBlurTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms(pssm, ssaoFlag);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16,1);
	}

}
