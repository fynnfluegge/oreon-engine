package org.oreon.gl.engine.antialiasing;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterR16F;

import lombok.Getter;

public class SampleCoverage {

	@Getter
	private GLTexture sampleCoverageMask;
	@Getter
	private GLTexture lightScatteringMaskDownSampled;
	private SampleCoverageShader shader;
	
	public SampleCoverage(int width, int height) {
		
		shader = SampleCoverageShader.getInstance();
		
		sampleCoverageMask = new Texture2DNoFilterR16F(width, height);
		lightScatteringMaskDownSampled = new Texture2DBilinearFilterRGBA16F(width, height);
	}
	
	public void render(GLTexture worldPositionTexture, GLTexture LightScatteringMaskMS) {
		
		shader.bind();
		glBindImageTexture(0, sampleCoverageMask.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_R16F);
		glBindImageTexture(1, worldPositionTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, lightScatteringMaskDownSampled.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(3, LightScatteringMaskMS.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		shader.updateUniforms();
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
	}

}
