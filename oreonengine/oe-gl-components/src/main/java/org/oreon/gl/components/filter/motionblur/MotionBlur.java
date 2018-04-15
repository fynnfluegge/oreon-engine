package org.oreon.gl.components.filter.motionblur;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA32F;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA16F;

import lombok.Getter;

public class MotionBlur {
	
	@Getter
	private GLTexture motionBlurSceneTexture;
	private GLTexture pixelVelocityTexture;
	private PixelVelocityShader pixelVelocityShader;
	private MotionBlurShader motionBlurShader;
	
	public MotionBlur() {
		pixelVelocityTexture = new Texture2DNoFilterRGBA32F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight());
		motionBlurSceneTexture = new Texture2DStorageRGBA16F(EngineContext.getWindow().getWidth(),
				EngineContext.getWindow().getHeight(),1);
		
		pixelVelocityShader = PixelVelocityShader.getInstance();
		motionBlurShader = MotionBlurShader.getInstance();
	}
	
	public void render(GLTexture depthmap, GLTexture sceneSampler) {
		
		pixelVelocityShader.bind();
		glBindImageTexture(0, pixelVelocityTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		pixelVelocityShader.updateUniforms(EngineContext.getCamera().getProjectionMatrix(), 
										   EngineContext.getCamera().getViewProjectionMatrix().invert(), 
										   EngineContext.getCamera().getPreviousViewProjectionMatrix(), 
										   depthmap);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		motionBlurSceneTexture.bind();
		
		motionBlurShader.bind();
		glBindImageTexture(0, motionBlurSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, pixelVelocityTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		motionBlurShader.updateUniforms(EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
	}

}
