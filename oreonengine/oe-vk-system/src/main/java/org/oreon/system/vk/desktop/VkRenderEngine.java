package org.oreon.system.vk.desktop;

import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.texture.Texture;

public class VkRenderEngine implements RenderEngine{

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
	public Framebuffer getDeferredFbo() {
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
	public Object getUnderwater() {
		// TODO Auto-generated method stub
		return null;
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
