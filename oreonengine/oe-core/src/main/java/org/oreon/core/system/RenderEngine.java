package org.oreon.core.system;

import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.math.Quaternion;
import org.oreon.core.texture.Texture;

public interface RenderEngine {

	public void init();
	public void render();
	public void update();
	public void shutdown();
	
	public boolean isWireframe();
	public boolean isCameraUnderWater();
	public boolean isWaterReflection();
	public boolean isWaterRefraction();
	public boolean isBloomEnabled();
	
	public Framebuffer getMultisampledFbo();
	public Framebuffer getDeferredFbo();
	public Texture getSceneDepthmap();
	public Quaternion getClipplane();
	public float getSightRangeFactor();
	public Object getUnderwater();
	
	public void setClipplane(Quaternion plane);
	public void setGrid(boolean flag);
	public void setWaterRefraction(boolean flag);
	public void setWaterReflection(boolean flag);
	public void setCameraUnderWater(boolean flag);	
	public void setSightRangeFactor(float range);
}
