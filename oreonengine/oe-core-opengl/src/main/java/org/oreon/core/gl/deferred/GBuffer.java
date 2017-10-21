package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL11.GL_RGBA8;

import java.nio.ByteBuffer;

import org.oreon.core.gl.texture.Texture2D;

public class GBuffer {

	private Texture2D albedoTexture;
	private Texture2D normalTexture;
	private Texture2D worldPositionTexture;
	private Texture2D SpecularEmissionTexture;
	private Texture2D sceneDepthmap;
	
	public GBuffer(int width, int height) {
		
		albedoTexture = new Texture2D();
		albedoTexture.generate();
		albedoTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		albedoTexture.bilinearFilter();
		albedoTexture.clampToEdge();
		
		normalTexture = new Texture2D();
		normalTexture.generate();
		normalTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		normalTexture.bilinearFilter();
		normalTexture.clampToEdge();
		
		worldPositionTexture = new Texture2D();
		worldPositionTexture.generate();
		worldPositionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		worldPositionTexture.bilinearFilter();
		worldPositionTexture.clampToEdge();
		
		SpecularEmissionTexture = new Texture2D();
		SpecularEmissionTexture.generate();
		SpecularEmissionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		SpecularEmissionTexture.bilinearFilter();
		SpecularEmissionTexture.clampToEdge();
		
		sceneDepthmap = new Texture2D();
		sceneDepthmap.generate();
		sceneDepthmap.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		sceneDepthmap.bilinearFilter();
		sceneDepthmap.clampToEdge();
	}
		
	public Texture2D getAlbedoTexture() {
		return albedoTexture;
	}
	public void setAlbedoTexture(Texture2D colorTexture) {
		this.albedoTexture = colorTexture;
	}
	public Texture2D getNormalTexture() {
		return normalTexture;
	}
	public void setNormalTexture(Texture2D normalTexture) {
		this.normalTexture = normalTexture;
	}
	public Texture2D getSpecularEmissionTexture() {
		return SpecularEmissionTexture;
	}
	public void setSpecularEmissionTexture(Texture2D specularEmissionTexture) {
		SpecularEmissionTexture = specularEmissionTexture;
	}
	public Texture2D getSceneDepthmap() {
		return sceneDepthmap;
	}
	public void setSceneDepthmap(Texture2D sceneDepthmap) {
		this.sceneDepthmap = sceneDepthmap;
	}
	public Texture2D getWorldPositionTexture() {
		return worldPositionTexture;
	}
	public void setWorldPositionTexture(Texture2D worldPositionTexture) {
		this.worldPositionTexture = worldPositionTexture;
	}
}
