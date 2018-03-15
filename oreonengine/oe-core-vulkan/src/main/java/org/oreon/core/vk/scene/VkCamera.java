package org.oreon.core.vk.scene;

import org.oreon.core.platform.GLFWInput;
import org.oreon.core.scenegraph.Camera;

public class VkCamera extends Camera{

	public VkCamera(GLFWInput input) {
		
		super();
		
		setInput(input);
	}
	
	@Override
	public void init() {
		
	}

}
