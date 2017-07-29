package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import engine.core.Camera;
import engine.core.Window;
import engine.shaders.lightScattering.SunLightScatteringAdditiveBlendShader;
import engine.shaders.lightScattering.SunLightScatteringShader;
import engine.textures.Texture2D;

public class SunLightScattering {

	private Texture2D sunLightScatteringTexture;
	private Texture2D sunLightScatteringSceneTexture;
	private SunLightScatteringShader lightScatteringShader;
	private SunLightScatteringAdditiveBlendShader additiveBlendShader;
	
	public SunLightScattering() {
		
		lightScatteringShader = SunLightScatteringShader.getInstance();
		additiveBlendShader = SunLightScatteringAdditiveBlendShader.getInstance();
		
		sunLightScatteringTexture = new Texture2D();
		sunLightScatteringTexture.generate();
		sunLightScatteringTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sunLightScatteringTexture.bilinearFilter();
		sunLightScatteringTexture.clampToEdge();
		
		sunLightScatteringSceneTexture = new Texture2D();
		sunLightScatteringSceneTexture.generate();
		sunLightScatteringSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sunLightScatteringSceneTexture.bilinearFilter();
		sunLightScatteringSceneTexture.clampToEdge();
	}
	
	public void render(Texture2D sceneSampler, Texture2D blackScene4lightScatteringSampler) {
		
		lightScatteringShader.bind();
		glBindImageTexture(0, blackScene4lightScatteringSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sunLightScatteringTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		lightScatteringShader.updateUniforms(Window.getInstance().getWidth(), Window.getInstance().getHeight(), Camera.getInstance().getViewProjectionMatrix());
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
		
		additiveBlendShader.bind();
		glBindImageTexture(0, sunLightScatteringTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, sunLightScatteringSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(Window.getInstance().getWidth()/8, Window.getInstance().getHeight()/8, 1);	
		glFinish();
	}
	
	public Texture2D getSunLightScatteringTexture() {
		return sunLightScatteringTexture;
	}

	public Texture2D getSunLightScatteringSceneTexture() {
		return sunLightScatteringSceneTexture;
	}

	public void setSunLightScatteringSceneTexture(Texture2D sunLightScatteringSceneTexture) {
		this.sunLightScatteringSceneTexture = sunLightScatteringSceneTexture;
	}
}
