package org.oreon.core.vk.context;

import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;

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
