package org.oreon.core.vk.core.context;

import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;

import lombok.Getter;

@Getter
public class VkDeviceContext {

	private VkInstance instance;
	private LogicalDevice logicalDevice;
	private PhysicalDevice physicalDevice;
	
	public VkDeviceContext() {
		// TODO Auto-generated constructor stub
	}
}
