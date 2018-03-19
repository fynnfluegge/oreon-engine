package org.oreon.core.vk.scenegraph;

import org.oreon.core.platform.GLFWInput;
import org.oreon.core.scenegraph.Camera;

public class VkCamera extends Camera{

	public VkCamera(GLFWInput input) {
		
		super();
		
		setInput(input);
		
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
