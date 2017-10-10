package org.oreon.system.gl.desktop;

import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.texture.Texture;
import org.oreon.modules.gl.gui.elements.TexturePanel;

public class GLDeferredRenderer implements RenderingEngine{

	private Window window;
	private TexturePanel fullScreenTexture;
	private Texture2D postProcessingTexture;

	private GLFramebuffer fbo;
	private GLFramebuffer multisampledFbo;
	private Texture2D colorTexture;
	private Texture2D normalTexture;
	private Texture2D SpecularEmissionTexture;
	private Texture2D sceneDepthmap;
	private Texture2D blackScene4LightScatteringTexture;
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isGrid() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCameraUnderWater() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWaterReflection() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWaterRefraction() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isBloomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Framebuffer getMultisampledFbo() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Texture getSceneDepthmap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Quaternion getClipplane() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setClipplane(Quaternion plane) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setGrid(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setWaterRefraction(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setWaterReflection(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCameraUnderWater(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setSightRangeFactor(float range) {
		// TODO Auto-generated method stub
		
	}
}
