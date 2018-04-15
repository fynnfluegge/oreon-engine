package org.oreon.gl.components.filter.lightscattering;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;

import lombok.Getter;

public class SunLightScattering {

	@Getter
	private GLTexture sunLightScatteringSceneTexture;
	
	private GLTexture sunLightScatteringTexture;
	private SunLightScatteringShader lightScatteringShader;
	private SunLightScatteringAdditiveBlendShader additiveBlendShader;
	
	public SunLightScattering() {
		
		lightScatteringShader = SunLightScatteringShader.getInstance();
		additiveBlendShader = SunLightScatteringAdditiveBlendShader.getInstance();
		
		sunLightScatteringTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight());
		sunLightScatteringTexture.bind();
		sunLightScatteringTexture.clampToEdge();
		sunLightScatteringTexture.unbind();
		
		sunLightScatteringSceneTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(),
						EngineContext.getWindow().getHeight());
		sunLightScatteringSceneTexture.bind();
		sunLightScatteringSceneTexture.clampToEdge();
		sunLightScatteringSceneTexture.unbind();
	}
	
	public void render(GLTexture sceneSampler, GLTexture lightScatteringMask) {
		
		lightScatteringShader.bind();
		glBindImageTexture(0, lightScatteringMask.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sunLightScatteringTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		lightScatteringShader.updateUniforms(EngineContext.getWindow().getWidth(), 
											 EngineContext.getWindow().getHeight(), 
										     EngineContext.getCamera().getViewProjectionMatrix());
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		additiveBlendShader.bind();
		glBindImageTexture(0, sunLightScatteringTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, sunLightScatteringSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}
	
}
