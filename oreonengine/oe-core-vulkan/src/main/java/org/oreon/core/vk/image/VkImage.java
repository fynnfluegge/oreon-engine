package org.oreon.core.vk.image;

import org.lwjgl.vulkan.VkImageCreateInfo;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateImage;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent3D;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;

public class VkImage {
	
	@Getter
	private long handle;

	public void create(VkDevice device, int width, int height, int depth){
		
		VkExtent3D extent = VkExtent3D.calloc()
				.width(width)
				.height(height)
				.depth(depth);
		
		VkImageCreateInfo createInfo = VkImageCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
				.imageType(VK_IMAGE_TYPE_2D)
				.extent(extent)
				.mipLevels(1)
				.arrayLayers(1)
				.format(VK_FORMAT_R8G8B8A8_UNORM)
				.tiling(VK_IMAGE_TILING_OPTIMAL)
				.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.usage(VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT)
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.samples(VK_SAMPLE_COUNT_1_BIT)
				.flags(0);
		
		LongBuffer pBuffer = memAllocLong(1);
	    int err = vkCreateImage(device, createInfo, null, pBuffer);
        handle = pBuffer.get(0);
    
        memFree(pBuffer);
        createInfo.free();
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create image: " + VkUtil.translateVulkanResult(err));
        }
	}
}
