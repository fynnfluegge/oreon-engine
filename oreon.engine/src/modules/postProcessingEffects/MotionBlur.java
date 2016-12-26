package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexParameteri;
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
import engine.texturing.Texture;

public class MotionBlur {
	
	private Texture motionBlurTexture;
	private Texture pixelVelocityMap;
	private PixelVelocityShader pixelVelocityShader;
	private MotionBlurShader motionBlurShader;
	private boolean enabled = true;
	
	public MotionBlur() {
		pixelVelocityMap = new Texture();
		pixelVelocityMap.generate();
		pixelVelocityMap.bind();
		pixelVelocityMap.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		motionBlurTexture = new Texture();
		motionBlurTexture.generate();
		motionBlurTexture.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		pixelVelocityShader = PixelVelocityShader.getInstance();
		motionBlurShader = MotionBlurShader.getInstance();
	}
	
	public void render(Texture depthmap, Texture sceneSampler) {
		
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
	
	public Texture getMotionBlurTexture() {
		return motionBlurTexture;
	}
	public void setMotionBlurTexture(Texture motionBlurTexture) {
		this.motionBlurTexture = motionBlurTexture;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Texture getPixelVelocityMap() {
		return pixelVelocityMap;
	}

	public void setPixelVelocityMap(Texture pixelVelocityMap) {
		this.pixelVelocityMap = pixelVelocityMap;
	}
	
}
