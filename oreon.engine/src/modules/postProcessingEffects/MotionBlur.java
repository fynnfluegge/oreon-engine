package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.lwjgl.input.Keyboard;

import engine.core.Camera;
import engine.core.Input;
import engine.core.Window;
import engine.shader.motionblur.MotionBlurShader;
import engine.shader.motionblur.PixelVelocityShader;
import engine.textures.Texture2D;

public class MotionBlur {
	
	private Texture2D motionBlurTexture;
	private Texture2D pixelVelocityMap;
	private PixelVelocityShader pixelVelocityShader;
	private MotionBlurShader motionBlurShader;
	private boolean enabled = false;
	
	public MotionBlur() {
		pixelVelocityMap = new Texture2D();
		pixelVelocityMap.generate();
		pixelVelocityMap.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		motionBlurTexture = new Texture2D();
		motionBlurTexture.generate();
		motionBlurTexture.bind();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		pixelVelocityShader = PixelVelocityShader.getInstance();
		motionBlurShader = MotionBlurShader.getInstance();
	}
	
	public void render(Texture2D depthmap, Texture2D sceneSampler) {
		
		pixelVelocityShader.bind();
		glBindImageTexture(0, pixelVelocityMap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		pixelVelocityShader.updateUniforms(Camera.getInstance().getProjectionMatrix(), Camera.getInstance().getViewProjectionMatrix().invert(), Camera.getInstance().getPreviousViewProjectionMatrix(), depthmap);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		motionBlurTexture.bind();
		
		motionBlurShader.bind();
		glBindImageTexture(0, motionBlurTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		glBindImageTexture(1, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(2, pixelVelocityMap.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		motionBlurShader.updateUniforms(Window.getInstance().getWidth(), Window.getInstance().getHeight());
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public void update()
	{
		if (Input.getKeyDown(Keyboard.KEY_B))
			if (enabled) enabled = false;
			else enabled = true;			
	}
	
	public Texture2D getMotionBlurTexture() {
		return motionBlurTexture;
	}
	public void setMotionBlurTexture(Texture2D motionBlurTexture) {
		this.motionBlurTexture = motionBlurTexture;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Texture2D getPixelVelocityMap() {
		return pixelVelocityMap;
	}

	public void setPixelVelocityMap(Texture2D pixelVelocityMap) {
		this.pixelVelocityMap = pixelVelocityMap;
	}
	
}
