package org.oreon.core.vk.core.context;

import org.oreon.core.platform.Camera;

public class VkCamera extends Camera{
	
//	private VkUniformBuffer uniformBuffer;

	public VkCamera() {
		  
		super();
		
		// flip y-axxis for vulkan coordinate system
		getProjectionMatrix().set(1, 1, -getProjectionMatrix().get(1, 1));
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void update(){
		
		super.update();
	}

}
