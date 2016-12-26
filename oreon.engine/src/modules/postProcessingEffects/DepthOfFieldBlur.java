package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import engine.core.Window;
import engine.shader.dofBlur.DepthOfFieldHorizontalBlurShader;
import engine.texturing.Texture;

public class DepthOfFieldBlur {
	
	private Texture dofBlurTexture;
	private DepthOfFieldHorizontalBlurShader dofBlurShader;
	
	private float[] gaussianKernel3 = {0.27901f,0.44198f,0.27901f};
	private float[] gaussianKernel5 = {0.06136f,0.24477f,0.38774f,0.24477f,0.06136f};
	private float[] gaussianKernel7 = {0.00598f,0.060626f,0.241843f,0.383103f,0.241843f,0.060626f,0.00598f};
	private float[] gaussianKernel9 = {0.000229f,0.005977f,0.060598f,0.241732f,0.382928f,0.241732f,0.060598f,0.005977f,0.000229f};
	
	private boolean enabled = true;
	
	public DepthOfFieldBlur() {
		
		dofBlurShader = DepthOfFieldHorizontalBlurShader.getInstance();
		dofBlurTexture = new Texture();
		dofBlurTexture.generate();
		dofBlurTexture.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, Window.getInstance().getWidth(), Window.getInstance().getHeight());
	}
	
	public void render(Texture depthmap, Texture sceneSampler) {
		
		dofBlurShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, dofBlurTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		dofBlurShader.updateUniforms(depthmap, gaussianKernel9);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture getDofBlurTexture() {
		return dofBlurTexture;
	}

	public void setDofBlurTexture(Texture dofBlurTexture) {
		this.dofBlurTexture = dofBlurTexture;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
