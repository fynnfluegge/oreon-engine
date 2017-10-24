package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32UI;

import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.util.Constants;

public class GBuffer {

	private Texture2DMultisample albedoTexture;
	private Texture2DMultisample normalTexture;
	private Texture2DMultisample worldPositionTexture;
	private Texture2DMultisample SpecularEmissionTexture;
	
	public GBuffer(int width, int height) {
		
		albedoTexture = new Texture2DMultisample();
		albedoTexture.generate();
		albedoTexture.bind();
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, Constants.MULTISAMPLES, GL_RGBA8, width, height, true);
		
		worldPositionTexture = new Texture2DMultisample();
		worldPositionTexture.generate();
		worldPositionTexture.bind();
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, Constants.MULTISAMPLES, GL_RGBA32F, width, height, true);
		
		normalTexture = new Texture2DMultisample();
		normalTexture.generate();
		normalTexture.bind();
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, Constants.MULTISAMPLES, GL_RGBA32F, width, height, true);
		
		SpecularEmissionTexture = new Texture2DMultisample();
		SpecularEmissionTexture.generate();
		SpecularEmissionTexture.bind();
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, Constants.MULTISAMPLES, GL_RGBA8, width, height, true);		
	}
		
	public Texture2DMultisample getAlbedoTexture() {
		return albedoTexture;
	}
	public void setAlbedoTexture(Texture2DMultisample colorTexture) {
		this.albedoTexture = colorTexture;
	}
	public Texture2DMultisample getNormalTexture() {
		return normalTexture;
	}
	public void setNormalTexture(Texture2DMultisample normalTexture) {
		this.normalTexture = normalTexture;
	}
	public Texture2DMultisample getSpecularEmissionTexture() {
		return SpecularEmissionTexture;
	}
	public void setSpecularEmissionTexture(Texture2DMultisample specularEmissionTexture) {
		SpecularEmissionTexture = specularEmissionTexture;
	}
	public Texture2DMultisample getWorldPositionTexture() {
		return worldPositionTexture;
	}
	public void setWorldPositionTexture(Texture2DMultisample worldPositionTexture) {
		this.worldPositionTexture = worldPositionTexture;
	}
}
