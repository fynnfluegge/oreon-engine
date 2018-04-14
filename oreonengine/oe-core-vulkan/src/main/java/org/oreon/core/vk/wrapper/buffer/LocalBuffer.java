package org.oreon.core.vk.wrapper.buffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.core.buffer.VkBuffer;

public class LocalBuffer extends VkBuffer{

	public LocalBuffer(VkDevice device,
			 				 VkPhysicalDeviceMemoryProperties memoryProperties,
			 				 int size,
			 				 int usage) {
		
		create(device, size, VK_BUFFER_USAGE_TRANSFER_DST_BIT | usage);
		allocateBuffer(memoryProperties, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
		bindBufferMemory();
	}
	
}
