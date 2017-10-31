package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.ByteBuffer;

import org.oreon.core.gl.texture.Texture2D;

public class TransparencyGBuffer {

	private Texture2D albedoTexture;
	private Texture2D worldPositionTexture;
	private Texture2D normalTexture;
	private Texture2D specularEmissionTexture;
	private Texture2D depthTexture;
	
	public TransparencyGBuffer(int width, int height) {
	
		albedoTexture = new Texture2D();
		albedoTexture.generate();
		albedoTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		albedoTexture.noFilter();
		
		worldPositionTexture = new Texture2D();
		worldPositionTexture.generate();
		worldPositionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		worldPositionTexture.noFilter();
		
		normalTexture = new Texture2D();
		normalTexture.generate();
		normalTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		normalTexture.noFilter();
		
		specularEmissionTexture = new Texture2D();
		specularEmissionTexture.generate();
		specularEmissionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		specularEmissionTexture.noFilter();
		
		depthTexture = new Texture2D();
		depthTexture.generate();
		depthTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		depthTexture.noFilter();
	}

	public Texture2D getAlbedoTexture() {
		return albedoTexture;
	}

	public void setAlbedoTexture(Texture2D albedoTexture) {
		this.albedoTexture = albedoTexture;
	}

	public Texture2D getWorldPositionTexture() {
		return worldPositionTexture;
	}

	public void setWorldPositionTexture(Texture2D worldPositionTexture) {
		this.worldPositionTexture = worldPositionTexture;
	}

	public Texture2D getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(Texture2D normalTexture) {
		this.normalTexture = normalTexture;
	}

	public Texture2D getSpecularEmissionTexture() {
		return specularEmissionTexture;
	}

	public void setSpecularEmissionTexture(Texture2D specularEmissionTexture) {
		this.specularEmissionTexture = specularEmissionTexture;
	}

	public Texture2D getDepthTexture() {
		return depthTexture;
	}

	public void setDepthTexture(Texture2D depthTexture) {
		this.depthTexture = depthTexture;
	}
}
