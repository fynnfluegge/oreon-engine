package org.oreon.core.vk.image;

import org.lwjgl.vulkan.VkImageCreateInfo;

import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D;

public class VkImage {

	public void create(){
		
		VkImageCreateInfo createInfo = VkImageCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
				.imageType(VK_IMAGE_TYPE_2D);
	}
}
