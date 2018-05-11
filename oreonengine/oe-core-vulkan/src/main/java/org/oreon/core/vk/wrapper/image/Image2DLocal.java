package org.oreon.core.vk.wrapper.image;

import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.image.VkImage;

public class Image2DLocal extends VkImage{

	public Image2DLocal(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
							  int width, int height, int format, int usage) {
		
		super(device, width, height, 1, format, usage, 1);
		allocate(memoryProperties, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
		bindImageMemory();
	}

}
