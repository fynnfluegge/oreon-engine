package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import engine.core.Camera;
import engine.core.Window;
import engine.shader.motionblur.MotionBlurShader;
import engine.shader.motionblur.PixelVelocityShader;
import engine.textures.Texture2D;

public class MotionBlur {
	
	private Texture2D motionBlurSceneTexture;
	private Texture2D pixelVelocityTexture;
	private PixelVelocityShader pixelVelocityShader;
	private MotionBlurShader motionBlurShader;
	
	public MotionBlur() {
		pixelVelocityTexture = new Texture2D();
		pixelVelocityTexture.generate();
		pixelVelocityTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		motionBlurSceneTexture = new Texture2D();
		motionBlurSceneTexture.generate();
		motionBlurSceneTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA16F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		pixelVelocityShader = PixelVelocityShader.getInstance();
		motionBlurShader = MotionBlurShader.getInstance();
	}
	
	public void render(Texture2D depthmap, Texture2D sceneSampler) {
		
		pixelVelocityShader.bind();
		glBindImageTexture(0, pixelVelocityTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		pixelVelocityShader.updateUniforms(Camera.getInstance().getProjectionMatrix(), Camera.getInstance().getViewProjectionMatrix().invert(), Camera.getInstance().getPreviousViewProjectionMatrix(), depthmap);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		motionBlurSceneTexture.bind();
		
		motionBlurShader.bind();
		glBindImageTexture(0, motionBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, pixelVelocityTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		motionBlurShader.updateUniforms(Window.getInstance().getWidth(), Window.getInstance().getHeight());
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getMotionBlurSceneTexture() {
		return motionBlurSceneTexture;
	}

	public Texture2D getPixelVelocityTexture() {
		return pixelVelocityTexture;
	}
}
