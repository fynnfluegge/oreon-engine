package org.oreon.modules.gl.postprocessfilter.motionblur;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DMultisample;

public class MotionBlur {
	
	private Texture2D motionBlurSceneTexture;
	private Texture2D pixelVelocityTexture;
	private PixelVelocityShader pixelVelocityShader;
	private MotionBlurShader motionBlurShader;
	
	public MotionBlur() {
		pixelVelocityTexture = new Texture2D();
		pixelVelocityTexture.generate();
		pixelVelocityTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		
		motionBlurSceneTexture = new Texture2D();
		motionBlurSceneTexture.generate();
		motionBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA16F, EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		
		pixelVelocityShader = PixelVelocityShader.getInstance();
		motionBlurShader = MotionBlurShader.getInstance();
	}
	
	public void render(Texture2DMultisample depthmap, Texture2D sceneSampler) {
		
		pixelVelocityShader.bind();
		glBindImageTexture(0, pixelVelocityTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		pixelVelocityShader.updateUniforms(EngineContext.getCamera().getProjectionMatrix(), 
										   EngineContext.getCamera().getViewProjectionMatrix().invert(), 
										   EngineContext.getCamera().getPreviousViewProjectionMatrix(), 
										   depthmap);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		motionBlurSceneTexture.bind();
		
		motionBlurShader.bind();
		glBindImageTexture(0, motionBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, pixelVelocityTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		motionBlurShader.updateUniforms(EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getMotionBlurSceneTexture() {
		return motionBlurSceneTexture;
	}

	public Texture2D getPixelVelocityTexture() {
		return pixelVelocityTexture;
	}
}
