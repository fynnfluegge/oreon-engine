package org.oreon.core.gl.deferred;

import org.oreon.core.gl.texture.Texture2D;

public class GBuffer {

	private Texture2D colorTexture;
	private Texture2D normalTexture;
	private Texture2D worldPositionTexture;
	private Texture2D SpecularEmissionTexture;
	private Texture2D sceneDepthmap;
	
	public Texture2D getColorTexture() {
		return colorTexture;
	}
	public void setColorTexture(Texture2D colorTexture) {
		this.colorTexture = colorTexture;
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
