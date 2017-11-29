package org.oreon.core.gl.antialiasing;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.oreon.core.gl.shaders.SampleCoverageMaskShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.system.CoreSystem;

public class MSAA {

	private SampleCoverageMaskShader shader;
	private Texture2D sampleCoverageMask;
	
	public MSAA() {
		
		shader = SampleCoverageMaskShader.getInstance();
		
		sampleCoverageMask = new Texture2D();
		sampleCoverageMask.generate();
		sampleCoverageMask.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_R16F,
						CoreSystem.getInstance().getWindow().getWidth(),
						CoreSystem.getInstance().getWindow().getHeight(),
						0, GL_RED, GL_FLOAT, (ByteBuffer) null);
		sampleCoverageMask.noFilter();
	}
	
	public void renderSampleCoverageMask(Texture2DMultisample worldPositionTexture,
										 Texture2DMultisample LightScatteringMaskMS,
										 Texture2D LightScatteringMask) {
		
		shader.bind();
		glBindImageTexture(0, sampleCoverageMask.getId(), 0, false, 0, GL_WRITE_ONLY, GL_R16F);
		glBindImageTexture(1, worldPositionTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, LightScatteringMask.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(3, LightScatteringMaskMS.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		shader.updateUniforms();
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/16, CoreSystem.getInstance().getWindow().getHeight()/16, 1);	
	}
	
	public Texture2D getSampleCoverageMask() {
		return sampleCoverageMask;
	}
	public void setSampleCoverageMask(Texture2D sampleCoverageMask) {
		this.sampleCoverageMask = sampleCoverageMask;
	}
}
